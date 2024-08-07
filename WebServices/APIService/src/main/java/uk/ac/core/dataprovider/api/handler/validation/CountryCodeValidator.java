package uk.ac.core.dataprovider.api.handler.validation;

import java.util.Arrays;
import java.util.Locale;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountryCodeValidator implements ConstraintValidator<CountryCode, String> {

    @Override
    public boolean isValid(String countryCode, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.asList(Locale.getISOCountries()).contains(countryCode.toUpperCase());
    }

    @Override
    public void initialize(CountryCode constraintAnnotation) {

    }
}
