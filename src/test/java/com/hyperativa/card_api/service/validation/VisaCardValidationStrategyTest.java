package com.hyperativa.card_api.service.validation;

import com.hyperativa.card_api.domain.exception.InvalidPanException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VisaCardValidationStrategy Tests")
class VisaCardValidationStrategyTest {
    
    private VisaCardValidationStrategy strategy = new VisaCardValidationStrategy();

    @Test
    void shouldThrowExceptionWhenPanIsTooShort() {
        String[] shortPans = {"", "1", "12", "123"};
        
        for (String shortPan : shortPans) {
            InvalidPanException exception = assertThrows(
                InvalidPanException.class,
                () -> strategy.validate(shortPan),
                "Should throw excepion for PAN: " + shortPan
            );
            
            assertEquals("PAN is too short", exception.getMessage());
        }
    }

    @Test
    void shouldThrowExceptionWhenPanIsTooLong() {
        String longPan = "12345678901234567890";
        String veryLongPan = "123456789012345678901234567890";
        
        InvalidPanException exception1 = assertThrows(
            InvalidPanException.class,
            () -> strategy.validate(longPan)
        );
        assertEquals("PAN is too long", exception1.getMessage());
        
        InvalidPanException exception2 = assertThrows(
            InvalidPanException.class,
            () -> strategy.validate(veryLongPan)
        );
        assertEquals("PAN is too long", exception2.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789012345a",
        "1234 5678 9012 3456",
        "1234-5678-9012-3456",
        "1234.5678.9012.3456",
        "1234567890123456!",
        "abcdefghijklmnop",
        "12345678901234567#8",
        "1234567890@gmail",
        "123456789012345$67"
    })
    void shouldThrowExceptionWhenPanContainsInvalidCharacters(String invalidPan) {
        InvalidPanException exception = assertThrows(
            InvalidPanException.class,
            () -> strategy.validate(invalidPan),
            "Should trhow invalida characters exception" + invalidPan
        );
        
        assertEquals("PAN contains invalid characters", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1234",
        "12345678901234567",
        "1234567890123456789",
        "4111111111111111",
        "0000000000000000"
    })
    void shouldAcceptValidPans(String validPan) {
        assertDoesNotThrow(
            () -> strategy.validate(validPan),
            "Não deveria lançar exceção para PAN válido: " + validPan
        );
    }
}