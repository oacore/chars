package uk.ac.core.languagedetection;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.languagenormalise.stringparsers.ToIso639_3;

/**
 *
 * @author lucas
 */
@Service
public class LanguageDetectionService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LanguageDetectionService.class);

    private org.apache.tika.language.detect.LanguageDetector tikaDetector;
    private final Random random;

    public LanguageDetectionService() {
        this.random = new Random();
        logger.info("First text processed takes a long time as the library is loaded on demand");
        this.tikaDetector = new OptimaizeLangDetector().loadModels();
    }

    /**
     * Identifies the language of the input text
     * @param text
     * @return ISO 639-3 code (3 letter language code)
     * @throws IOException
     */
    public String detectLanguage(String text) throws IOException {
        long start = System.currentTimeMillis();
        String result = this.tikaDetector.detect(text).getLanguage();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        logger.info("Tika took: {}ms | language: {} | length: {} ", timeElapsed, result, text.length());

        ToIso639_3 iso = new ToIso639_3(result);
        Optional<String> language = iso.patternMatch();
        if (language.isPresent()) {
            return language.get();
        }
        return null;
    }
}
