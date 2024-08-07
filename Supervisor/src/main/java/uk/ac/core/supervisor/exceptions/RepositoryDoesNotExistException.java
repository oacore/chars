package uk.ac.core.supervisor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.core.common.exceptions.CHARSException;

/**
 *
 * @author lucasanastasiou
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such repository")  // 404
public class RepositoryDoesNotExistException extends CHARSException {

    public RepositoryDoesNotExistException(int id) {
        super("Repository with id " + id + " does not exist");
    }

}
