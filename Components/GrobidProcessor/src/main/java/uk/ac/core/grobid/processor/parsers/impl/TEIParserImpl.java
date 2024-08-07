/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.grobid.processor.parsers.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.GrobidAffiliationAuthor;
import uk.ac.core.common.model.GrobidAffiliationDepartment;
import uk.ac.core.common.model.GrobidAffiliationHeaderAuthor;
import uk.ac.core.common.model.GrobidAffiliationInstitution;
import uk.ac.core.common.model.GrobidAffiliationLaboratory;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.AddrLine;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Address;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Analytic;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Author;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Back;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.BiblScope;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.BiblStruct;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Date;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Div;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Forename;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Idno;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Imprint;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.ListBibl;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Meeting;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Monogr;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.PersName;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Publisher;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Surname;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.TEI;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Title;
import uk.ac.core.common.model.GrobidCitation;
import uk.ac.core.common.model.GrobidCitationAuthor;
import uk.ac.core.common.model.GrobidCitationBiblScope;
import uk.ac.core.common.model.GrobidCitationImprint;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Affiliation;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Country;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Email;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.OrgName;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.PostCode;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.RoleName;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Settlement;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.SourceDesc;
import uk.ac.core.grobid.processor.parsers.TEIParser;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Service
public class TEIParserImpl implements TEIParser{
    
    Logger logger = LoggerFactory.getLogger(TEIParserImpl.class);
    
    @Override
    public ListBibl getListBibl(TEI teiObject) {
        List<Object> anchors = teiObject.getText().getLinksAndAnchorsAndNotes();

        for (Object anchor : anchors) {
            if (anchor instanceof Back) {
                Back back = (Back) anchor;
                List<Object> headsAndPS = back.getHeadsAndPSAndTrashes();
                for (Object headAndPS : headsAndPS) {
                    if (headAndPS instanceof Div) {
                        Div div = (Div) headAndPS;
                        List<Object> meetingsAndHeadsAndLinks = div.getMeetingsAndHeadsAndLinks();
                        for (Object meetingsAndHeadsAndLink : meetingsAndHeadsAndLinks) {
                            if (meetingsAndHeadsAndLink instanceof ListBibl) {
                                ListBibl listBibl = (ListBibl) meetingsAndHeadsAndLink;
                                return listBibl;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Author> getListHeaderAuthors(TEI teiObject) {
        List<Author> authorList = new ArrayList<>();
        List<SourceDesc> sourceDescs = teiObject.getTeiHeader().getFileDesc().getSourceDescs();
        for(SourceDesc sourceDesc : sourceDescs){
            List<Object> sourceDescItemsList = sourceDesc.getBiblsAndBiblStructsAndListBibls();
            for(Object sourceDescItems : sourceDescItemsList){
                if(sourceDescItems instanceof BiblStruct){
                    BiblStruct biblStruct = (BiblStruct) sourceDescItems;
                    List<Analytic> analytictsList = biblStruct.getAnalytics();
                    for(Analytic analytic : analytictsList){
                        List<Object> authorEditorTitleList = analytic.getAuthorsAndEditorsAndTitles();
                        for(Object analyticItem : authorEditorTitleList){
                            if(analyticItem instanceof Author){
                                Author author = (Author) analyticItem;
                                authorList.add(author);
                            }
                        }
                        if(!authorList.isEmpty()) return authorList;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<GrobidCitation> getListOfGrobidCitationsFromListBibl(Integer articleId, ListBibl listBibl) throws IllegalAccessException {
        List<GrobidCitation> results = new ArrayList<>();
        List<Object> listOfBibStructs = listBibl.getBiblsAndBiblStructsAndListBibls();
        Integer positionInBiblList = 0;
        
        for (Object bibStructItem : listOfBibStructs) {
            BiblStruct biblStruct = (BiblStruct) bibStructItem;
            List<GrobidCitationAuthor> grobidAuthors = new ArrayList<>();
            GrobidCitation grobidCitation = new GrobidCitation();
            
            grobidCitation.setArticleId(articleId);
            grobidCitation.setXmlId(biblStruct.getId());
            grobidCitation.setPositionInBiblList(positionInBiblList);
            
            for(Field bibStructItemPart : bibStructItem.getClass().getDeclaredFields()){
                if (bibStructItemPart.getName().equalsIgnoreCase("analytics") || bibStructItemPart.getName().equalsIgnoreCase("monogrsAndSeries")) {
                    bibStructItemPart.setAccessible(true); 
                    ArrayList bibStructItemPartList = (ArrayList) bibStructItemPart.get(bibStructItem);
                    if(bibStructItemPartList == null){
                        continue;
                    }
                    List<Object> authorsAndTitlesAndEditors = null;
                    int authorCounter = 0;
                    int authorsNum = 0;
                    
                    if (bibStructItemPart.getName().equalsIgnoreCase("analytics")){
                        Analytic analyticOrMonogrObj = (Analytic) bibStructItemPartList.get(0);
                        authorsAndTitlesAndEditors = analyticOrMonogrObj.getAuthorsAndEditorsAndTitles();
                    }else if(bibStructItemPart.getName().equalsIgnoreCase("monogrsAndSeries")){
                        Monogr analyticOrMonogrObj = (Monogr) bibStructItemPartList.get(0);
                        authorsAndTitlesAndEditors = analyticOrMonogrObj.getImprintsAndAuthorsAndEditors();
                    }
                    
                    for (Object authorsAndTitlesAndEditor : authorsAndTitlesAndEditors) {
                        if (authorsAndTitlesAndEditor instanceof Author){
                            authorsNum++;
                        }
                    }
                    
                    for (Object authorsAndTitlesAndEditor : authorsAndTitlesAndEditors) {
                        if (authorsAndTitlesAndEditor instanceof Title){
                            Title title = (Title) authorsAndTitlesAndEditor;
                            ArrayList titleList = (ArrayList) title.getContent();
                            if(titleList.size() > 0) grobidCitation.setTitle(titleList.get(0).toString());
                        }
                        
                        if (authorsAndTitlesAndEditor instanceof Meeting){
                            Meeting meeting = (Meeting) authorsAndTitlesAndEditor;
                            List<Object> meetingList = meeting.getContent();
                            for (Object meetingListItem : meetingList) {
                                if (meetingListItem instanceof Address){
                                    Address address = (Address) meetingListItem;
                                    List<Object> addrLines = address.getLinksAndAnchorsAndNotes();
                                    for (Object addrLinesItem : addrLines) {
                                        AddrLine addrLine = (AddrLine) addrLinesItem;
                                        ArrayList addrLineList = (ArrayList) addrLine.getContent();
                                        if(addrLineList.size() > 0) grobidCitation.setAddress(addrLineList.get(0).toString());
                                    }
                                }
                            }
                        }
                        
                        if (authorsAndTitlesAndEditor instanceof Imprint){
                            GrobidCitationImprint grobidImprint = new GrobidCitationImprint();
                            Imprint imprint = (Imprint) authorsAndTitlesAndEditor;
                            List<Object> imprintList = imprint.getBiblScopesAndDatesAndPubPlaces();
                            List<GrobidCitationBiblScope> grobidBibleScopes = new ArrayList<>();
                            int biblScopeCounter = 0;
                            
                            for (Object imprintListItem : imprintList) {
                                if (imprintListItem instanceof Date){
                                    Date date = (Date) imprintListItem;
                                    grobidImprint.setDate(date.getWhen());
                                    grobidImprint.setDateType(date.getType());
                                }
                                if (imprintListItem instanceof Publisher){
                                    Publisher publisher = (Publisher) imprintListItem;
                                    ArrayList publisherList = (ArrayList) publisher.getContent();
                                    if(publisherList.size() > 0) grobidImprint.setPublisher(publisherList.get(0).toString());
                                }
                                if (imprintListItem instanceof BiblScope){
                                    biblScopeCounter++;
                                    String biblScopeStr = "";
                                    BiblScope biblScope = (BiblScope) imprintListItem;
                                    GrobidCitationBiblScope grobidBiblScope = new GrobidCitationBiblScope();
                                    
                                    String unitStr = biblScope.getUnit();
                                    grobidBiblScope.setUnit(unitStr);
                                    if(unitStr != null && unitStr.equalsIgnoreCase("page")){
                                        biblScopeStr = (biblScopeCounter <= 1) ? "pp. " : "";
                                    }
                                    
                                    String fromStr = biblScope.getFrom();
                                    grobidBiblScope.setUnitFrom(fromStr);
                                    if(fromStr != null) biblScopeStr += fromStr + "-";
                                    
                                    String toStr = biblScope.getTo();
                                    grobidBiblScope.setUnitTo(toStr);
                                    if(toStr != null) biblScopeStr += toStr;
                                    
                                    grobidBiblScope.setType(biblScope.getType());
                                    ArrayList biblScopeList = (ArrayList) biblScope.getContent();
                                    if(biblScopeList.size() > 0){
                                        String contentStr = biblScopeList.get(0).toString();
                                        grobidBiblScope.setContent(contentStr);
                                        biblScopeStr += contentStr;
                                    }
                                    grobidBibleScopes.add(grobidBiblScope);
                                    grobidImprint.addGrobidBiblScopesStr(biblScopeStr);
                                }
                            }
                            grobidImprint.setGrobidBiblScopes(grobidBibleScopes);
                            grobidCitation.setGrobidCitationImprint(grobidImprint);
                        }
                        
                        if (authorsAndTitlesAndEditor instanceof Author){
                            Author author = (Author) authorsAndTitlesAndEditor;
                            List<Object> authorList = author.getContent();
                            authorCounter++;
                            
                            for (Object authorListItem : authorList) {
                                if (authorListItem instanceof PersName){
                                    String authorStr = "";
                                    String surnameStr = "";
                                    String firstNameList = "";
                                    String middleNameList = "";
                                    
                                    GrobidCitationAuthor grobidAuthor = new GrobidCitationAuthor();
                                    PersName persName = (PersName)authorListItem;
                                    for (Object persNameItem : persName.getContent()) {
                                        if (persNameItem instanceof Surname){
                                            Surname surname = (Surname) persNameItem;
                                            ArrayList surnameList = (ArrayList)surname.getContent();
                                            if(surnameList.size() > 0){
                                                surnameStr = surnameList.get(0).toString();
                                                grobidAuthor.setSurname(surnameStr);
                                            }
                                        }
                                        if (persNameItem instanceof Forename){
                                            Forename forename = (Forename) persNameItem;
                                            if(forename.getType().equalsIgnoreCase("first")){
                                                ArrayList forenameList = (ArrayList)forename.getContent();
                                                if(forenameList.size() > 0){
                                                    String firstName = forenameList.get(0).toString();
                                                    grobidAuthor.setForenameFirst(firstName);
                                                    if(firstName.length() == 1) firstName += ".";
                                                    firstNameList += firstName;
                                                }
                                            }
                                            if(forename.getType().equalsIgnoreCase("middle")){
                                                ArrayList forenameList = (ArrayList)forename.getContent();
                                                if(forenameList.size() > 0){
                                                    String middleName = forenameList.get(0).toString();
                                                    grobidAuthor.setForenameMiddle(middleName);
                                                    if(middleName.length() == 1) middleName += ".";
                                                    middleNameList += middleName;
                                                }
                                            }
                                        }
                                    }
                                    if(!firstNameList.isEmpty()) firstNameList = " " + firstNameList;
                                    authorStr += surnameStr + firstNameList + middleNameList;
                                    boolean isLastAuthor = false;
                                    if(authorCounter == authorsNum && authorList.size() > 1) isLastAuthor = true;
                                    grobidCitation.addAuthorToAuthorsStr(authorStr, isLastAuthor);
                                    grobidAuthors.add(grobidAuthor);
                                }
                            }
                        }
                        
                        if (authorsAndTitlesAndEditor instanceof Idno){
                            Idno idno = (Idno) authorsAndTitlesAndEditor;
                            if(idno.getType().equalsIgnoreCase("DOI")){
                                ArrayList idnoList = (ArrayList) idno.getContent();
                                if(idnoList.size() > 0) grobidCitation.setDoi(idnoList.get(0).toString());
                            }
                        }
                    }
                }
                grobidCitation.setGrobidAuthors(grobidAuthors);
            }
            results.add(grobidCitation);
            positionInBiblList++;
        }
        
        return results;
    }
    
    @Override
    public List<GrobidAffiliationHeaderAuthor> processingGrobidHeaderAuthors(Integer articleId, List<Author> headerAuthorList){
        List<GrobidAffiliationHeaderAuthor> affiliationHeaderAuthors = new ArrayList<>();
        
        int headerAuthorIterator = 0;
        for(Author headerAuthor : headerAuthorList){
            int authorIterator = 0;
            int institutionIterator = 0;
            
            String affiliationDepartmentsStr = "";
            String affiliationLabsStr = "";
            String affiliationAddressStr = "";
            String affiliationCountryStr = "";
            String affiliationContactStr = "";
            
            List<GrobidAffiliationAuthor> affiliationAuthors = new ArrayList<>();
            List<GrobidAffiliationInstitution> affiliationInstitutions = new ArrayList<>();
            
            GrobidAffiliationHeaderAuthor affiliationHeaderAuthor = new GrobidAffiliationHeaderAuthor();
            affiliationHeaderAuthor.setHeaderAuthorId(headerAuthorIterator);
            
            List<Object> headherAuthorContentList = headerAuthor.getContent();
            for(Object headherAuthorContentItem : headherAuthorContentList){
                
                // Author
                if(headherAuthorContentItem instanceof PersName){
                    authorIterator++;
                    GrobidAffiliationAuthor affiliationAuthor = new GrobidAffiliationAuthor();
                    affiliationAuthor.setPosition(authorIterator);
                    
                    PersName persName = (PersName) headherAuthorContentItem;
                    for(Object persNameItem : persName.getContent()){
                        if (persNameItem instanceof Surname){
                            Surname surname = (Surname) persNameItem;
                            ArrayList surnameList = (ArrayList)surname.getContent();
                            if(surnameList.size() > 0){
                                affiliationAuthor.setSurname(surnameList.get(0).toString());
                            }
                        }
                        if (persNameItem instanceof Forename){
                            Forename forename = (Forename) persNameItem;
                            if(forename.getType().equalsIgnoreCase("first")){
                                ArrayList forenameList = (ArrayList)forename.getContent();
                                if(forenameList.size() > 0){
                                    String firstName = forenameList.get(0).toString();
                                    if(firstName.length() == 1) firstName += ".";
                                    affiliationAuthor.setForenameFirst(firstName);
                                }
                            }
                            if(forename.getType().equalsIgnoreCase("middle")){
                                ArrayList forenameList = (ArrayList)forename.getContent();
                                if(forenameList.size() > 0){
                                    String middleName = forenameList.get(0).toString();
                                    if(middleName.length() == 1) middleName += ".";
                                    affiliationAuthor.setForenameMiddle(middleName);
                                }
                            }
                        }
                        if (persNameItem instanceof RoleName){
                            RoleName roleName = (RoleName) persNameItem;
                            ArrayList roleNameList = (ArrayList)roleName.getContent();
                            if(roleNameList.size() > 0){
                                affiliationAuthor.setRolename(roleNameList.get(0).toString());
                            }
                        }
                    }
                    affiliationAuthors.add(affiliationAuthor);
                }
                
                // Author
                if(headherAuthorContentItem instanceof Email){
                    Email email = (Email) headherAuthorContentItem;
                    ArrayList emailList = (ArrayList)email.getContent();
                    if(emailList.size() > 0){
                        affiliationContactStr += emailList.get(0).toString();
                    }
                    for(GrobidAffiliationAuthor author : affiliationAuthors){
                        author.setContact(affiliationContactStr);
                    }
                }
                
                // Institution, Department, Laboratory
                if(headherAuthorContentItem instanceof Affiliation){
                    
                    Affiliation affiliation = (Affiliation) headherAuthorContentItem;
                    for(Object affiliationItem : affiliation.getContent()){
                        
                        if(affiliationItem instanceof OrgName){
                            OrgName orgName = (OrgName) affiliationItem;
                            
                            if(orgName.getType() == null) continue;
                            
                            // institution
                            if(orgName.getType().equalsIgnoreCase("institution")){
                                institutionIterator++;
                                
                                GrobidAffiliationInstitution affiliationInstitution = new GrobidAffiliationInstitution();
                                affiliationInstitution.setPosition(institutionIterator);
                                
                                ArrayList institutionList = (ArrayList)orgName.getContent();
                                if(institutionList.size() > 0){
                                    affiliationInstitution.setName(institutionList.get(0).toString());
                                }
                                affiliationInstitutions.add(affiliationInstitution);
                            }
                            
                            // department
                            if(orgName.getType().equalsIgnoreCase("department")){
                                ArrayList departmentList = (ArrayList)orgName.getContent();
                                if(departmentList.size() > 0){
                                    affiliationDepartmentsStr += departmentList.get(0).toString() + ";\n";
                                }
                            }
                            
                            // laboratory
                            if(orgName.getType().equalsIgnoreCase("laboratory")){
                                ArrayList laboratoryList = (ArrayList)orgName.getContent();
                                if(laboratoryList.size() > 0){
                                    affiliationLabsStr += laboratoryList.get(0).toString() + ";\n";
                                }
                            }
                        }
                        
                        // address
                        if(affiliationItem instanceof Address){
                            Address address = (Address) affiliationItem;
                            List<Object> addressItems = address.getLinksAndAnchorsAndNotes();
                            for(Object addressItem : addressItems){
                                
                                if(addressItem instanceof AddrLine){
                                    AddrLine addrLine = (AddrLine) addressItem;
                                    ArrayList addrLineList = (ArrayList)addrLine.getContent();
                                    if(addrLineList.size() > 0){
                                        affiliationAddressStr += addrLineList.get(0).toString() + "\n";
                                    }
                                }
                                
                                if(addressItem instanceof PostCode){
                                    PostCode postCode = (PostCode) addressItem;
                                    String postCodeStr= postCode.getContent();
                                    if(!postCodeStr.isEmpty()){
                                        affiliationAddressStr += postCodeStr + "\n";
                                    }
                                }
                                
                                if(addressItem instanceof Settlement){
                                    Settlement settlement = (Settlement) addressItem;
                                    ArrayList settlementList = (ArrayList)settlement.getContent();
                                    if(settlementList.size() > 0){
                                        affiliationAddressStr += settlementList.get(0).toString() + "\n";
                                    }
                                }
                                
                                if(addressItem instanceof Country){
                                    Country country = (Country) addressItem;
                                    affiliationCountryStr = country.getKey();
                                    ArrayList countryList = (ArrayList)country.getContent();
                                    if(countryList.size() > 0){
                                        affiliationAddressStr += countryList.get(0).toString() + "\n";
                                    }
                                }
                                
                            }
                        }
                    }
                }
                
            }
            
            for(GrobidAffiliationInstitution institution : affiliationInstitutions){
                institution.setInstitutionId((articleId.toString() + headerAuthorIterator + institutionIterator).hashCode());
                institution.setDocumentId(articleId);
                institution.setAddress(affiliationAddressStr);
                institution.setCountry(affiliationCountryStr);
                institution.setGrobidAffiliationDepartmentsStr(affiliationDepartmentsStr);
                institution.setGrobidAffiliationLabsStr(affiliationLabsStr);
            }
            
            affiliationHeaderAuthor.setDocumentId(articleId);
            affiliationHeaderAuthor.setHeaderAuthorId(headerAuthorIterator);
            affiliationHeaderAuthor.setGrobidAffiliationAuthors(affiliationAuthors);
            affiliationHeaderAuthor.setGrobidAffiliationInstitutions(affiliationInstitutions);
            
            affiliationHeaderAuthors.add(affiliationHeaderAuthor);
            headerAuthorIterator++;
        }
        return affiliationHeaderAuthors;
    }
}
