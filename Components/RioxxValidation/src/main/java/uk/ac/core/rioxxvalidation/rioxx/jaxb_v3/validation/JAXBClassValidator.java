/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author mc26486
 */
public class JAXBClassValidator {
    private static final Logger logger = LoggerFactory.getLogger(JAXBClassValidator.class);
    private final SimpleDateFormat expectedDateFormat;

    private static List<String> iso6392languages3chars = Arrays.asList("aar", "abk", "ace", "ach", "ada", "ady", "afa", "afh", "afr", "ain", "aka", "akk", "alb", "sqi", "ale", "alg", "alt", "amh", "ang", "anp", "apa", "ara", "arc", "arg", "arm", "hye", "arn", "arp", "art", "arw", "asm", "ast", "ath", "aus", "ava", "ave", "awa", "aym", "aze", "bad", "bai", "bak", "bal", "bam", "ban", "baq", "eus", "bas", "bat", "bej", "bel", "bem", "ben", "ber", "bho", "bih", "bik", "bin", "bis", "bla", "bnt", "tib", "bod", "bos", "bra", "bre", "btk", "bua", "bug", "bul", "bur", "mya", "byn", "cad", "cai", "car", "cat", "cau", "ceb", "cel", "cze", "ces", "cha", "chb", "che", "chg", "chi", "zho", "chk", "chm", "chn", "cho", "chp", "chr", "chu", "chv", "chy", "cmc", "cnr", "cop", "cor", "cos", "cpe", "cpf", "cpp", "cre", "crh", "crp", "csb", "cus", "wel", "cym", "cze", "ces", "dak", "dan", "dar", "day", "del", "den", "ger", "deu", "dgr", "din", "div", "doi", "dra", "dsb", "dua", "dum", "dut", "nld", "dyu", "dzo", "efi", "egy", "eka", "gre", "ell", "elx", "eng", "enm", "epo", "est", "baq", "eus", "ewe", "ewo", "fan", "fao", "per", "fas", "fat", "fij", "fil", "fin", "fiu", "fon", "fre", "fra", "fre", "fra", "frm", "fro", "frr", "frs", "fry", "ful", "fur", "gaa", "gay", "gba", "gem", "geo", "kat", "ger", "deu", "gez", "gil", "gla", "gle", "glg", "glv", "gmh", "goh", "gon", "gor", "got", "grb", "grc", "gre", "ell", "grn", "gsw", "guj", "gwi", "hai", "hat", "hau", "haw", "heb", "her", "hil", "him", "hin", "hit", "hmn", "hmo", "hrv", "hsb", "hun", "hup", "arm", "hye", "iba", "ibo", "ice", "isl", "ido", "iii", "ijo", "iku", "ile", "ilo", "ina", "inc", "ind", "ine", "inh", "ipk", "ira", "iro", "ice", "isl", "ita", "jav", "jbo", "jpn", "jpr", "jrb", "kaa", "kab", "kac", "kal", "kam", "kan", "kar", "kas", "geo", "kat", "kau", "kaw", "kaz", "kbd", "kha", "khi", "khm", "kho", "kik", "kin", "kir", "kmb", "kok", "kom", "kon", "kor", "kos", "kpe", "krc", "krl", "kro", "kru", "kua", "kum", "kur", "kut", "lad", "lah", "lam", "lao", "lat", "lav", "lez", "lim", "lin", "lit", "lol", "loz", "ltz", "lua", "lub", "lug", "lui", "lun", "luo", "lus", "mac", "mkd", "mad", "mag", "mah", "mai", "mak", "mal", "man", "mao", "mri", "map", "mar", "mas", "may", "msa", "mdf", "mdr", "men", "mga", "mic", "min", "mis", "mac", "mkd", "mkh", "mlg", "mlt", "mnc", "mni", "mno", "moh", "mon", "mos", "mao", "mri", "may", "msa", "mul", "mun", "mus", "mwl", "mwr", "bur", "mya", "myn", "myv", "nah", "nai", "nap", "nau", "nav", "nbl", "nde", "ndo", "nds", "nep", "new", "nia", "nic", "niu", "dut", "nld", "nno", "nob", "nog", "non", "nor", "nqo", "nso", "nub", "nwc", "nya", "nym", "nyn", "nyo", "nzi", "oci", "oji", "ori", "orm", "osa", "oss", "ota", "oto", "paa", "pag", "pal", "pam", "pan", "pap", "pau", "peo", "per", "fas", "phi", "phn", "pli", "pol", "pon", "por", "pra", "pro", "pus", "qaa-qtz", "que", "raj", "rap", "rar", "roa", "roh", "rom", "rum", "ron", "rum", "ron", "run", "rup", "rus", "sad", "sag", "sah", "sai", "sal", "sam", "san", "sas", "sat", "scn", "sco", "sel", "sem", "sga", "sgn", "shn", "sid", "sin", "sio", "sit", "sla", "slo", "slk", "slo", "slk", "slv", "sma", "sme", "smi", "smj", "smn", "smo", "sms", "sna", "snd", "snk", "sog", "som", "son", "sot", "spa", "alb", "sqi", "srd", "srn", "srp", "srr", "ssa", "ssw", "suk", "sun", "sus", "sux", "swa", "swe", "syc", "syr", "tah", "tai", "tam", "tat", "tel", "tem", "ter", "tet", "tgk", "tgl", "tha", "tib", "bod", "tig", "tir", "tiv", "tkl", "tlh", "tli", "tmh", "tog", "ton", "tpi", "tsi", "tsn", "tso", "tuk", "tum", "tup", "tur", "tut", "tvl", "twi", "tyv", "udm", "uga", "uig", "ukr", "umb", "und", "urd", "uzb", "vai", "ven", "vie", "vol", "vot", "wak", "wal", "war", "was", "wel", "cym", "wen", "wln", "wol", "xal", "xho", "yao", "yap", "yid", "yor", "ypk", "zap", "zbl", "zen", "zgh", "zha", "chi", "zho", "znd", "zul", "zun", "zxx", "zza");

    public JAXBClassValidator() {
        this.expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.expectedDateFormat.setLenient(false);
    }

    public ValidationReport validate(Rioxx instance) throws IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        ValidationReport validationReport = new ValidationReport();

        validationReport.setMissingRequiredData(findMissingFields(instance));
        validationReport.setMissingRequiredData(validateRequiredData(instance, validationReport.getMissingRequiredData()));
        validationReport.setMissingOptionalData(validateOptionalData(instance));

        return validationReport;
    }

    private Map<String, List<String>> findMissingFields(Rioxx instance) throws IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        Map<String, List<String>> missingFields = new HashMap<>();
        Class<? extends Rioxx> newClass = instance.getClass();

        for (Field field : newClass.getDeclaredFields()) {
            String name = field.getName();
            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof RioxxCompliance_v3) {
                    RioxxCompliance_v3 rioxxCompliance = (RioxxCompliance_v3) annotation;

                    if (!rioxxCompliance.minOccur().equals("0")) {
                        Integer minOccur = Integer.parseInt(rioxxCompliance.minOccur());
                        if (isFieldNotValidByMinOccur(field, newClass, instance, minOccur)) {
                            missingFields.computeIfAbsent(name, n -> new ArrayList<>())
                                    .add(name + " is missing");
                        }
                    }
                    if (!rioxxCompliance.maxOccur().equals("unbounded")) {
                        Integer maxOccur = Integer.parseInt(rioxxCompliance.maxOccur());
                        if (isFieldNotValidByMaxOccur(field, newClass, instance, maxOccur)) {
                            missingFields.computeIfAbsent(name, n -> new ArrayList<>())
                                    .add(name + " should not be greater then " + maxOccur);
                        }
                    }
                    break;
                }
            }
        }
        return missingFields;
    }

    private Map<String, List<String>> validateRequiredData(Rioxx instance, Map<String, List<String>> result) {
        checkLicenseRef(instance, result);
        checkLanguage(instance, result);
        checkIdentifier(instance, result);
        checkGrant(instance, result);
        checkProject(instance, result);
        checkType(instance, result);

        return result;
    }

    private Map<String, List<String>> validateOptionalData(Rioxx instance) {
        Map<String, List<String>> result = new HashMap<>();

        checkDescription(instance, result);
        checkPublisher(instance, result);
        checkRelation(instance, result);
        checkExtRelation(instance, result);
        checkSource(instance, result);
        checkCreator(instance, result);
        checkContributor(instance, result);
        checkPublicationDate(instance, result);
        checkRecordPublicReleaseDate(instance, result);
        checkDateAccepted(instance, result);
        checkSubject(instance, result);
        return result;
    }


    //todo: MUST be taken from the COAR Controlled Vocabulary for Resource Type Genres (Version 3.0)
    private void checkType(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getType() != null) {
            if (isNotAnUri(instance.getType())) {
                warningMap.computeIfAbsent("type", n -> new ArrayList<>())
                        .add("Uri is either empty or is not an HTTP(S) URI");
                logger.warn("Uri for Type is not HTTP(S) URI");
            }
        }

        if (instance.getType() != null && !instance.getType().contains("//purl.org/coar/resource_type/")) {
            warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                    .add("Relation COAR type must be from the COAR resource type list");
            logger.warn("Relation COAR type must be from the COAR resource type list");
        }
    }

    private void checkRecordPublicReleaseDate(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getRecordPublicReleaseDate() != null) {
            try {
                new TextToDateTime(instance.getRecordPublicReleaseDate().trim()).asLocalDateTime();
            } catch (Exception e) {
                warningMap.computeIfAbsent("recordPublicReleaseDate", n -> new ArrayList<>())
                        .add("record_public_release_date doesn't follow format YYYY-MM-DD");
                logger.warn("record_public_release_date doesn't follow format YYYY-MM-DD");
            }
        }
    }

    private void checkPublicationDate(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getPublicationDate() != null) {
            try {
                new TextToDateTime(instance.getPublicationDate().trim()).asLocalDateTime();
            } catch (Exception e) {
                warningMap.computeIfAbsent("publicationDate", n -> new ArrayList<>())
                        .add("publication_date doesn't follow format YYYY-MM-DD");
                logger.warn("publication_date doesn't follow format YYYY-MM-DD");
            }
        }
    }

    private void checkProject(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getProject() != null) {
            if (instance.getProject().stream().anyMatch(project -> project.trim().isEmpty())) {
                warningMap.computeIfAbsent("project", n -> new ArrayList<>())
                        .add("Project content is empty");
                logger.warn("Project content is empty");
            }
            if (instance.getProject().stream().anyMatch(project -> isNotAnUri(project.trim()))) {
                warningMap.computeIfAbsent("project", n -> new ArrayList<>())
                        .add("Project is not an URI");
                logger.warn("Project is not an URI");
            }
        }
    }

    private void checkGrant(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getGrant() != null) {

            if (instance.getGrant().stream().anyMatch(grant -> grant.getValue() == null)) {
                warningMap.computeIfAbsent("grant", n -> new ArrayList<>())
                        .add("The property of Grant doesn't contain grant ID");
                logger.warn("The property of Grant doesn't contain grant ID");
            }

            if (instance.getGrant().stream().anyMatch(grant -> grant.getFunderId() == null && grant.getFunderName() == null)) {
                warningMap.computeIfAbsent("grant", n -> new ArrayList<>())
                        .add("Both funder_name and funder_id attributes are empty");
                logger.warn("Both funder_name and funder_id attributes are empty");
            }

            if (instance.getGrant().stream().anyMatch(grant -> grant.getFunderId() != null && isNotAnUri(grant.getFunderId()))) {
                warningMap.computeIfAbsent("grant", n -> new ArrayList<>())
                        .add("Funder_id is not HTTP(S) URI");
                logger.warn("Funder_id is not HTTP(S) URI");
            }
        }
    }


    private void checkContributor(Rioxx instance, Map<String, List<String>> warningMap) {

        if (instance.getContributor() != null &&
                instance.getContributor().stream().anyMatch(contributor -> contributor.getId() != null && contributor.getId().stream().allMatch(id -> isNotAnUri(id.getValue())))) {
            warningMap.computeIfAbsent("contributor", n -> new ArrayList<>())
                    .add("Contributor ID is not an URI");
            logger.warn("Contributor ID is not an URI");
        }
    }

    private void checkCreator(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getCreator() != null && !instance.getCreator().stream().anyMatch(creator -> creator.isFirstNamedAuthor())) {
            warningMap.computeIfAbsent("creator", n -> new ArrayList<>())
                    .add("first-named-author is not specified for any of the creators");
            logger.warn("first-named-author is not specified for any of the creators");
        }
        if (instance.getCreator() != null && instance.getCreator().stream().anyMatch(creator -> creator.getId() != null && creator.getId().stream().anyMatch(id -> isNotAnUri(id.getValue())))) {
            warningMap.computeIfAbsent("contributor", n -> new ArrayList<>())
                    .add("Creator ID is not an URI");
            logger.warn("Creator ID is not an URI");
        }
    }


    private void checkDateAccepted(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getDateAccepted() != null) {
            String dateAccepted = instance.getDateAccepted();
            try {
                expectedDateFormat.parse(dateAccepted);
            } catch (ParseException e) {
                warningMap.computeIfAbsent("dateAccepted", n -> new ArrayList<>())
                        .add("date_accepted doesn't follow format YYYY-MM-DD");
                logger.warn("date_accepted doesn't follow format YYYY-MM-DD");
            }
        }
    }

    private void checkSource(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getSource() != null) {
            String source = instance.getSource();
            source = source.replaceAll("-", "");
            if (source.length() != 8 && source.length() != 13) {
                warningMap.computeIfAbsent("source", n -> new ArrayList<>())
                        .add("Source isn't the unique 8-digit International Standard Serial Numbers (ISSN) " +
                                "or the 13 digit International Standard Book Number (ISBN13)");
                logger.warn("Source isn't the unique 8-digit International Standard Serial Numbers (ISSN) " +
                        "or the 13 digit International Standard Book Number (ISBN13)");
            }
        }
    }

    private void checkSubject(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getSubject() != null) {

            instance.getSubject().forEach(subject -> {
                if (isNotAnUri(subject)) {
                    warningMap.computeIfAbsent("subject", n -> new ArrayList<>())
                            .add("It is recommended that subject is an URI");
                    logger.warn("It is recommended that subject is an URI");
                }
            });
        }
    }


    private void checkLicenseRef(Rioxx instance, Map<String, List<String>> warningMap) {
    }


    private void checkLanguage(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getLanguage() != null && instance.getLanguage().stream()
                .anyMatch(l -> !isLanguageCorrect(l))) {
            warningMap.computeIfAbsent("language", n -> new ArrayList<>())
                    .add("Language doesn't conform to ISO 639-3");
            logger.warn("Language doesn't conform to ISO 639-3");
        }
    }

    private void checkIdentifier(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getIdentifier() != null) {
            if (isNotAnUri(instance.getIdentifier())) {
                warningMap.computeIfAbsent("identifier", n -> new ArrayList<>())
                        .add("Identifier is not a HTTP(S) URI");
                logger.warn("Identifier is not a HTTP(S) URI");
            }
        }
    }

    private void checkRelation(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getRelation() != null) {
            instance.getRelation().forEach(r -> {

                if (r.getValue() == null) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("Relation doesn't contain an URI");
                    logger.warn("Relation doesn't contain an URI");
                }

                if (r.getValue() != null && isNotAnUri(r.getValue())) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("URI for Relation is not a HTTP(S) URI");
                    logger.warn("URI for Relation is not a HTTP(S) URI");
                }


                if (r.getRel() != null && !IANATypeList.contains(r.getRel())) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("Relation rel attribute must be from the IANA relation type list");
                    logger.warn("Relation rel attribute must be from the IANA relation type");
                }

                if (r.getType() != null && !r.getType().contains("/")) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("Relation type must be a MIME type");
                    logger.warn("Relation type must be a MIME type");
                }

                checkCOARTypeAndVersion(warningMap, r.getCoarType(), r.getCoarVersion(), "Relation");

                if (r.getDepositDate() != null) {
                    try {
                        expectedDateFormat.parse(r.getDepositDate());
                    } catch (ParseException e) {
                        warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                                .add("deposit_date doesn't follow format YYYY-MM-DD");
                        logger.warn("deposit_date for relation doesn't follow format YYYY-MM-DD");
                    }
                }

                if (r.getResourceExposedDate() != null) {
                    try {
                        expectedDateFormat.parse(r.getResourceExposedDate());
                    } catch (ParseException e) {
                        warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                                .add("resource_exposed_date doesn't follow format YYYY-MM-DD");
                        logger.warn("resource_exposed_date for relation doesn't follow format YYYY-MM-DD");
                    }
                }
                if (r.getAccessType() != null && !r.getAccessType().contains("//purl.org/coar/access_right/")) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("Relation COAR version must be from the COAR version type list");
                    logger.warn("Relation COAR version must be from the COAR version type list");
                }
                if (r.getLicenseRef() != null && isNotAnUri(r.getLicenseRef())) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("Relation COAR version must be from the COAR version type list");
                    logger.warn("Relation COAR version must be from the COAR version type list");
                }
            });
        }
    }

    private void checkExtRelation(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getExt_relation() != null) {
            instance.getExt_relation().forEach(r -> {

                if (r.getValue() == null) {
                    warningMap.computeIfAbsent("ext_relation", n -> new ArrayList<>())
                            .add("External Relation doesn't contain an URI");
                    logger.warn("External Relation doesn't contain an URI");
                }

                if (r.getValue() != null && isNotAnUri(r.getValue())) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("URI for External Relation is not a HTTP(S) URI");
                    logger.warn("URI for External Relation is not a HTTP(S) URI");
                }


                if (r.getRel() != null && !IANATypeList.contains(r.getRel())) {
                    warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                            .add("External Relation rel " + r.getRel() + " attribute must be from the IANA relation type list");
                    logger.warn("External Relation rel attribute must be from the IANA relation type");
                }

                checkCOARTypeAndVersion(warningMap, r.getCoarType(), r.getCoarVersion(), "External Relation");
            });
        }
    }

    private void checkCOARTypeAndVersion(Map<String, List<String>> warningMap, String coarType, String coarVersion, String elementName) {
        if (coarType != null && !coarType.contains("//purl.org/coar/resource_type/")) {
            warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                    .add(elementName + " COAR type must be from the COAR resource type list");
            logger.warn(elementName + " COAR type must be from the COAR resource type list");
        }

        if (coarVersion != null && !coarVersion.contains("//purl.org/coar/version/")) {
            warningMap.computeIfAbsent("relation", n -> new ArrayList<>())
                    .add(elementName + "COAR version must be from the COAR version type list");
            logger.warn(elementName + " COAR version must be from the COAR version type list");
        }
    }

    public boolean isLanguageCorrect(String language) {
        if (language.length() == 2) {
            return Arrays.stream(Locale.getISOLanguages()).anyMatch(l -> l.equalsIgnoreCase(language));

        } else {
            return iso6392languages3chars.stream().anyMatch(l -> l.equalsIgnoreCase(language));
        }

    }


    private void checkDescription(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getDescription() != null) {
            instance.getDescription().forEach(d -> {
                if (d.matches("<([^\\s]+)(\\s[^>]*?)?(?<!/)>")) {
                    warningMap.computeIfAbsent("description", n -> new ArrayList<>())
                            .add("Field description contains markup tags");
                    logger.warn("Field description contains markup tags");
                }
            });
        }
    }

    private void checkPublisher(Rioxx instance, Map<String, List<String>> warningMap) {
        if (instance.getPublisher() != null && instance.getPublisher().stream().anyMatch(publisher -> publisher.getId() != null && publisher.getId().stream().anyMatch(id -> isNotAnUri(id.getValue())))) {
            warningMap.computeIfAbsent("publisher", n -> new ArrayList<>())
                    .add("Publisher ID is not an URI");
            logger.warn("Publisher ID is not an URI");
        }
    }


    private boolean isFieldNotValidByMinOccur(Field field, Class newClass, Object instance, Integer minOccur) throws
            SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object result = getValueForField(field, newClass, instance);
        if (result == null) {
            return true;
        } else if (result instanceof List) {
            List<Object> resultList = (List<Object>) result;
            return resultList.isEmpty() || resultList.size() < minOccur;
        } else if (result instanceof String) {
            String resultStr = (String) result;
            return resultStr.isEmpty() || resultStr.length() < minOccur;
        }
        return false;
    }

    private boolean isFieldNotValidByMaxOccur(Field field, Class newClass, Object instance, Integer maxOccur) throws
            SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        boolean missingFieldBasic = false;
        Object result = getValueForField(field, newClass, instance);
        if (result instanceof List) {
            List<Object> resultList = (List<Object>) result;
            if (resultList.isEmpty() || resultList.size() > maxOccur) {
                missingFieldBasic = true;
            }
        }
        return missingFieldBasic;
    }

    private Object getValueForField(Field field, Class newClass, Object instance) throws
            InvocationTargetException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {
        Class[] noparams = {};
        String fieldNameForMethod = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method method = newClass.getDeclaredMethod("get" + fieldNameForMethod);
        return method.invoke(instance, (Object[]) noparams);
    }

    private boolean isNotAnUri(String string) {
        return (string == null || !string.trim().startsWith("http"));
    }
}
