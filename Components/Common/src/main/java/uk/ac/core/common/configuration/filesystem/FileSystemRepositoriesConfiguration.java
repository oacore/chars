package uk.ac.core.common.configuration.filesystem;

import java.util.Map;

/**
 *
 * @author mc26486
 */
public class FileSystemRepositoriesConfiguration {

    public Map<Integer, FileSystemRepositoryConfig> repositories_configs;

    public Map<Integer, FileSystemRepositoryConfig> getRepositories_configs() {
        return repositories_configs;
    }

    public void setRepositories_configs(Map<Integer, FileSystemRepositoryConfig> repositories_configs) {
        this.repositories_configs = repositories_configs;
    }

    public FileSystemRepositoryConfig getRepositoryConfigById(int repositoryId) {
        return repositories_configs.get(repositoryId);
    }

}
