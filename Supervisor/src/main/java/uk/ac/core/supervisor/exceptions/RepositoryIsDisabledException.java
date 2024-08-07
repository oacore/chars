package uk.ac.core.supervisor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.core.common.exceptions.CHARSException;

/**
 *
 * @author lucasanastasiou
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Repository is disabled")
public class RepositoryIsDisabledException extends CHARSException {

    public RepositoryIsDisabledException(Integer repositoryId) {

    }
}
