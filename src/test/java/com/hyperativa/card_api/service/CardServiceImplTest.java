package com.hyperativa.card_api.service;

import com.hyperativa.card_api.domain.Card;
import com.hyperativa.card_api.domain.exception.DuplicateCardException;
import com.hyperativa.card_api.domain.exception.NotFoundException;
import com.hyperativa.card_api.dto.CardResponseDTO;
import com.hyperativa.card_api.infrastructure.repository.CardRepository;
import com.hyperativa.card_api.service.validation.CardValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardValidationService cardValidationService;

    private final String VALID_PAN = "1234567890123456";
    private final UUID MOCK_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    @DisplayName("Should register a card successfully when not exists in database")
    void shouldRegisterCardSuccessfully() {
        Card savedCard = Card.builder().pan(VALID_PAN).id(MOCK_UUID).build();
        when(cardRepository.existsByPan(VALID_PAN)).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        UUID resultId = cardService.registerCard(VALID_PAN);

        assertNotNull(resultId);
        assertEquals(MOCK_UUID, resultId);

        verify(cardValidationService).validatePan(VALID_PAN);
        verify(cardRepository).existsByPan(VALID_PAN);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    @DisplayName("Should save card with correct pan hash")
    void shouldSetPanHashBeforeSaving() {
        when(cardRepository.existsByPan(VALID_PAN)).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> {
            Card c = i.getArgument(0);
            return c;
        });

        cardService.registerCard(VALID_PAN);

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardCaptor.capture());

        Card capturedCard = cardCaptor.getValue();
        assertEquals(VALID_PAN, capturedCard.getPan());
    }

    @Test
    @DisplayName("Should throw exception when card is duplicated")
    void shouldThrowExceptionWhenCardIsDuplicate() {
        when(cardRepository.existsByPan(VALID_PAN)).thenReturn(true);

        assertThrows(DuplicateCardException.class, () -> cardService.registerCard(VALID_PAN));

        verify(cardValidationService).validatePan(VALID_PAN);
        verify(cardRepository).existsByPan(VALID_PAN);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenValidationFails() {
        String invalidPan = "123";
        doThrow(new IllegalArgumentException("Invalid PAN format"))
                .when(cardValidationService).validatePan(invalidPan);

        assertThrows(IllegalArgumentException.class, () -> cardService.registerCard(invalidPan));

        verify(cardRepository, never()).existsByPan(any());
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return cardDto when pan is found id database")
    void shouldReturnCardDtoWhenFound() {
        Card existingCard = Card.builder().pan(VALID_PAN).id(MOCK_UUID).build();
        when(cardRepository.findByPan(VALID_PAN)).thenReturn(Optional.of(existingCard));

        CardResponseDTO response = cardService.getCardId(VALID_PAN);

        assertNotNull(response);
        assertEquals(MOCK_UUID.toString(), response.cardUuid());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCardMissing() {
        when(cardRepository.findByPan(VALID_PAN)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> cardService.getCardId(VALID_PAN));

        assertEquals("Card not found", exception.getMessage());
    }
}