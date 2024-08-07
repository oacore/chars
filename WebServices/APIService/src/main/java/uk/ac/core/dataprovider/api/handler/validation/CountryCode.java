package uk.ac.core.dataprovider.api.handler.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CountryCodeValidator.class)
public @interface CountryCode {

    String message() default "can contain only ISO 3166 two-letter country codes";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
