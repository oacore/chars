/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface RioxxCompliance {

    String fullMinOccur() default "0";

    String fullMaxOccur() default "unbounded";

    String basicMinOccur() default "0";

    String basicMaxOccur() default "unbounded";
}
