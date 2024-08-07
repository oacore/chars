package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import uk.ac.core.rioxxvalidation.rioxx.ComplianceCheckerListener;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity.Rioxx;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.JAXBClassValidator;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class ComplianceCheckerV3 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ComplianceCheckerV3.class);


    private JAXBClassValidator validator;
    private Unmarshaller jaxbUnmarshaller;
    private ComplianceCheckerListener complianceCheckerListener;

    public ComplianceCheckerV3(ComplianceCheckerListener complianceCheckerListener){
        try{
            validator = new JAXBClassValidator();
            JAXBContext jaxbContext = JAXBContext.newInstance(Rioxx.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.complianceCheckerListener = complianceCheckerListener;

        } catch (JAXBException ex){
            logger.error("Error while creating JAXBContext", ex);
        }
    }

    public ValidationReport check(String rioxxRecord, String oai){
        ValidationReport validationReport = new ValidationReport();
        try {
            rioxxRecord=rioxxRecord.replace("xmlns=\"http://www.rioxx.net/schema/v3.0/rioxx/\"", "");
            rioxxRecord=rioxxRecord.replace("http://docs.rioxx.net/schema/v3.0/rioxxterms/", "http://www.rioxx.net/schema/v3.0/rioxxterms/");
            if (rioxxRecord.contains("xsi:schemaLocation") && !rioxxRecord.contains("xmlns:xsi="))
            {
                rioxxRecord=rioxxRecord.replace("xsi:schemaLocation", " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation" );
            }
            System.out.println(rioxxRecord);
            Source xmlFile = new StreamSource(IOUtils.toInputStream(rioxxRecord, "UTF-8"));
            Rioxx instance = (Rioxx) jaxbUnmarshaller.unmarshal(xmlFile);

            validationReport = validator.validate(instance);
            validationReport.setRecordIdentifier(oai);
            if(complianceCheckerListener != null){
                complianceCheckerListener.updateCompliance(validationReport);
            }
        } catch (Exception ex){
            logger.error("Error while validating report", ex);
            validationReport.setParseFailed(true);
        }
        return validationReport;
    }
}
