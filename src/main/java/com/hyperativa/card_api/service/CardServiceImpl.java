package com.hyperativa.card_api.service;

import com.hyperativa.card_api.domain.Card;
import com.hyperativa.card_api.domain.exception.DuplicateCardException;
import com.hyperativa.card_api.domain.exception.NotFoundException;
import com.hyperativa.card_api.dto.CardResponseDTO;
import com.hyperativa.card_api.infrastructure.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService{

    private final CardRepository repository;
    private final CardValidationService cardValidationService;

    @Transactional
    public UUID registerCard(String pan) {

        cardValidationService.validatePan(pan);

        if (repository.existsByPan(pan)) {
            throw new DuplicateCardException("Card already registered");
        }
        var card = Card.builder().pan(pan).build();
        card.setPanHash(pan);

        return repository.save(card).getId();
    }


    @Transactional(readOnly = true)
    public CardResponseDTO getCardId(String pan) {
        var card = repository.findByPan(pan)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        return new CardResponseDTO(card.getId().toString());
    }
}