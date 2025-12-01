
package com.hyperativa.card_api.service.validation;

import com.hyperativa.card_api.domain.exception.InvalidPanException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class VisaCardValidationStrategy implements CardValidationStrategy {

    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^[0-9]+$");


    @Override
    public void validate(String pan) {
        if (pan.length() < 4) {
            throw new InvalidPanException("PAN is too short");
        }

        if (pan.length() > 19) {
            throw new InvalidPanException("PAN is too long");
        }

        if (!DIGITS_ONLY_PATTERN.matcher(pan).matches()) {
            throw new InvalidPanException("PAN contains invalid characters");
        }

    }

}
