package com.hyperativa.card_api.service.validation;

import com.hyperativa.card_api.domain.exception.InvalidPanException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CardValidationServiceImpl implements CardValidationService{
    
    private final VisaCardValidationStrategy visaStrategy;
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^[0-9]+$");
    
    public void validatePan(String pan) {
        if (pan == null || pan.trim().isEmpty()) {
            throw new InvalidPanException("PAN cannot be empty.");
        }

        if (isVisa(pan)) {
            visaStrategy.validate(pan);
        } 

        else {
            validateDefault(pan);
        }
    }

    private boolean isVisa(String pan) {
        return true;
    }
    
    private void validateDefault(String pan) {
        if (pan.length() < 13 || pan.length() > 19) {
            throw new InvalidPanException("PAN must be between 13 and 19 digits long, inclusive.");
        }
        
        if (!DIGITS_ONLY_PATTERN.matcher(pan).matches()) {
            throw new InvalidPanException("PAN should contain only digits, no spaces or other characters.");
        }
    }
}
