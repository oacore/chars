/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxcomplianceworker.controller;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.rioxxcomplianceworker.worker.RioxxComplianceWorker;
import uk.ac.core.rioxxvalidation.rioxx.ComplianceCheckerListener;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation.ValidationReport;
import uk.ac.core.worker.QueueWorker;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author mc26486
 */
@RestController
public class StarterController {

    @Autowired
    QueueWorker queueWorker;

    @RequestMapping("/rioxx_compliance/{repositoryId}")
    public String document_download_starter(@PathVariable(value = "repositoryId") final Integer repositoryId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.RIOXX_COMPLIANCE);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @RequestMapping("/rioxx_compliance/file/{filename}")
    public String rioxxComplianceFileStarter(@PathVariable(value = "filename") final String filename) throws UnsupportedEncodingException {
        RioxxComplianceWorker rioxxComplianceWorker = (RioxxComplianceWorker) this.queueWorker;
        rioxxComplianceWorker.setMetadataPath(filename);
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            rioxxComplianceWorker.setSaxParser(saxParser);

        } catch (ParserConfigurationException | SAXException ex) {
            System.out.println("ex = " + ex);
        }
        Map<String, uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport > reportMap = new HashMap<>();
        rioxxComplianceWorker.setComplianceCheckerListener(new ComplianceCheckerListener() {
            @Override
            public void updateCompliance(ValidationReport validationReport) {
                System.out.println("validationReport = " + validationReport);
            }

            @Override
            public void updateCompliance(uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport validationReport) {
                System.out.println("validationReport = " + validationReport);
                reportMap.put(validationReport.getRecordIdentifier(), validationReport);
            }
        });

        rioxxComplianceWorker.process(Collections.EMPTY_LIST);
        return new Gson().toJson(reportMap);
    }

}