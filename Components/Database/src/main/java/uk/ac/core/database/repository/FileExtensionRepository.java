package uk.ac.core.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.core.database.entity.FileExtension;


public interface FileExtensionRepository extends JpaRepository<FileExtension, Integer> {

}