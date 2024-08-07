package uk.ac.core.supervisor.client.exceptions;

import uk.ac.core.common.exceptions.CHARSException;

/**
 *
 * @author lucasanastasiou
 */
public class ComponentNotReachableException extends CHARSException {

    public ComponentNotReachableException() {
        super("Cannot reach component");
    }
}
