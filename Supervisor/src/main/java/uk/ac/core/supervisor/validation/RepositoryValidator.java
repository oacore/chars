package uk.ac.core.supervisor.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.database.service.repositories.RepositoriesDAO;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

/**
 *
 * @author lucasanastasiou
 */
@Component
public class RepositoryValidator {
    
    @Autowired
    RepositoriesDAO repositoriesDAO;
    
    public void validate(int repositoryId) throws RepositoryDoesNotExistException,RepositoryIsDisabledException {

        if (!repositoriesDAO.repositoryExists(repositoryId)) {
            throw new RepositoryDoesNotExistException(repositoryId);
        }
        if (!repositoriesDAO.isRepositoryEnabled(repositoryId)) {
            throw new RepositoryIsDisabledException(repositoryId);
        }
    }

}
    