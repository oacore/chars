/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.issueDetection.util;

import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.core.issueDetection.model.CompactIssueBO;

public class GetIssueIdUtil {

    public static String getIssueId(CompactIssueBO issueBO) {
        return getIssueId(issueBO.getDocumentId(), issueBO.getRepositoryId(), issueBO.getType());
    }

    public static String getIssueId(long documentId, long repositoryId, IssueType issueType) {
        return DigestUtils.md5Hex((documentId == 0) ? "" : documentId + repositoryId + issueType.toString());
    }
}