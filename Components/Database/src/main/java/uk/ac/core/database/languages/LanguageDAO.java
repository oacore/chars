package uk.ac.core.database.languages;

import uk.ac.core.common.model.legacy.Language;

import java.util.Optional;

/**
 *
 * @author mc26486
 */
public interface LanguageDAO {

    /***
     * Get Language by ISO 639-1 2 letter code
     * @param code
     * @return
     */
    Optional<Integer> getIdByCode(String code);

    /***
     * Get Language by ISO 639-1 3 letter code
     * @param iso639_3
     * @return
     */
    Optional<Integer> getIdByIso639_3(String iso639_3);

    Optional<Language> getByPartialName(String name);

    /**
     * Adds the language for the document
     * @param documentId
     * @param code the 2 letter ISO 639-1 code
     */
    void insertLanguageForDocumentBy2LetterCountryCode(final Integer documentId, final String code);


    /**
     * Insert language by ISO 639-2 3 letter code
     * @param documentId
     * @param iso639_2
     */
    void insertLanguageForDocumentByIso639_2Code(final Integer documentId, final String iso639_2);

    Language getById(Integer id);

}
