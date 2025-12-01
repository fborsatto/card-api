package com.hyperativa.card_api.service;

import com.hyperativa.card_api.domain.exception.InvalidPanException;
import com.hyperativa.card_api.service.validation.VisaCardValidationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardValidationServiceImplTest {

    @Mock
    private VisaCardValidationStrategy visaStrategy;

    @InjectMocks
    private CardValidationServiceImpl service;

    @Test
    void shouldThrowExceptionWhenPanIsNull() {
        InvalidPanException exception = assertThrows(
                InvalidPanException.class,
                () -> service.validatePan(null)
        );

        assertEquals("PAN cannot be empty.", exception.getMessage());
        verifyNoInteractions(visaStrategy);
    }

    @Test
    void shouldUseVisaStrategyWhenPanIsVisa() {
        String validVisaPan = "4111111111111111";
        doNothing().when(visaStrategy).validate(validVisaPan);
        assertDoesNotThrow(() -> service.validatePan(validVisaPan));
        verify(visaStrategy, times(1)).validate(validVisaPan);
    }

    @Test
    void shouldPropagateVisaStrategyException() {
        String invalidVisaPan = "4111111111111110";
        InvalidPanException expectedException = new InvalidPanException("Invalid Visa PAN");
        doThrow(expectedException).when(visaStrategy).validate(invalidVisaPan);

        InvalidPanException exception = assertThrows(
                InvalidPanException.class,
                () -> service.validatePan(invalidVisaPan)
        );

        assertEquals(expectedException, exception);
        verify(visaStrategy, times(1)).validate(invalidVisaPan);
    }
}
