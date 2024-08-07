/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation;

import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.RioxxCompliance;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity.dc.elements._1.SimpleLiteral;

import javax.xml.bind.JAXBElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author mc26486
 */
public class JAXBClassValidator {

    public ValidationReport validate(Object instance) throws IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, SecurityException {

        Class newClass = instance.getClass();
        ValidationReport validationReport = new ValidationReport();
        for (Field field : newClass.getDeclaredFields()) {
            Class type = field.getType();
            String name = field.getName();
            Annotation[] annotations = field.getDeclaredAnnotations();
            boolean missingFieldFull = false;
            boolean missingFieldBasic = false;
            for (Annotation annotation : annotations) {
                if (annotation instanceof RioxxCompliance) {
                    RioxxCompliance rioxxCompliance = (RioxxCompliance) annotation;
                    if (!rioxxCompliance.basicMinOccur().equals("0")) {
                        Integer minOccur = Integer.parseInt(rioxxCompliance.basicMinOccur());
                        missingFieldBasic = checkMinOccur(field, newClass, instance, minOccur);
                    }
                    if (!rioxxCompliance.fullMinOccur().equals("0")) {
                        Integer minOccur = Integer.parseInt(rioxxCompliance.fullMinOccur());
                        missingFieldFull = checkMinOccur(field, newClass, instance, minOccur);
                    }
                    if (!rioxxCompliance.basicMaxOccur().equals("unbounded")){
                        Integer maxOccur = Integer.parseInt(rioxxCompliance.basicMaxOccur());
                        missingFieldFull = checkMaxOccur(field, newClass, instance, maxOccur);
                    }
                    if (!rioxxCompliance.fullMaxOccur().equals("unbounded")){
                        Integer maxOccur = Integer.parseInt(rioxxCompliance.basicMaxOccur());
                        missingFieldFull = checkMaxOccur(field, newClass, instance, maxOccur);
                    }
                    break;
                }
            }
            if (missingFieldFull) {
                validationReport.getMissingRequiredFieldFull().add(name);
            }
            if (missingFieldBasic) {
                validationReport.getMissingRequiredFieldBasic().add(name);
            }

        }
        return validationReport;
    }

    private boolean checkMinOccur(Field field, Class newClass, Object instance, Integer minOccur) throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        boolean missingFieldBasic = false;
        Object result = getValueForField(field, newClass, instance);
        if (result == null) {
            missingFieldBasic = true;
        } else if (result instanceof List) {
            List<Object> resultList = (List<Object>) result;
            if (resultList.isEmpty() || resultList.size()<minOccur) {
                missingFieldBasic = true;
            }
        } else if (minOccur==1 && result instanceof JAXBElement) {
            JAXBElement<SimpleLiteral> jAXBElement = (JAXBElement<SimpleLiteral>) result;
            if (jAXBElement.isNil() || jAXBElement.getValue() == null) {
                missingFieldBasic = true;
            }
        }
        return missingFieldBasic;
    }
    
    private boolean checkMaxOccur(Field field, Class newClass, Object instance, Integer maxOccur) throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        boolean missingFieldBasic = false;
        Object result = getValueForField(field, newClass, instance);
        if (result == null) {
            missingFieldBasic = true;
        } else if (result instanceof List) {
            List<Object> resultList = (List<Object>) result;
            if (resultList.isEmpty() || resultList.size()>maxOccur) {
                missingFieldBasic = true;
            }
        } else if (result instanceof JAXBElement) {
            JAXBElement<SimpleLiteral> jAXBElement = (JAXBElement<SimpleLiteral>) result;
            if (jAXBElement.isNil() || jAXBElement.getValue() == null) {
                missingFieldBasic = true;
            }
        }
        return missingFieldBasic;
    }

    private Object getValueForField(Field field, Class newClass, Object instance) throws InvocationTargetException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {
        Class noparams[] = {};
        String fieldNameForMethod = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method method = newClass.getDeclaredMethod("get" + fieldNameForMethod);
        return method.invoke(instance, (Object[]) noparams);
    }

    public JAXBClassValidator() {
    }

}
