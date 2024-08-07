package uk.ac.core.database.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.core.database.entity.Language;


public interface LanguageRepository extends JpaRepository<Language, Integer> {

    Language findOneByCode(String code);

    @Cacheable(value = "languageCache", key = "#iso639part3")
    Language findOneByIso639part3(String iso639part3);
}