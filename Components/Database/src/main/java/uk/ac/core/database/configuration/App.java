//package uk.ac.core.database.configuration;
//
//import java.util.List;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ApplicationContext;
//import uk.ac.core.common.model.legacy.ArticleMetadata;
//import uk.ac.core.common.model.legacy.Citation;
//import uk.ac.core.common.model.legacy.Language;
//import uk.ac.core.database.service.citation.CitationDAO;
//import uk.ac.core.database.service.document.RepositoryDocumentDAO;
//import uk.ac.core.database.service.duplicates.DuplicatesDAO;
//import uk.ac.core.database.service.journals.JournalsDAO;
//import uk.ac.core.database.service.repositories.RepositoriesDAO;
//
///**
// *
// * @author lucasanastasiou
// */
//@SpringBootApplication
//public class App {
//
//    public static void main(String[] args) {
//        ApplicationContext ctx = SpringApplication.run(App.class);
//        
//        DuplicatesDAO duplicatesDAO = (DuplicatesDAO)ctx.getBean(DuplicatesDAO.class);
//        Integer id = duplicatesDAO.getParentId(4);
//        System.out.println("id = " + id);
//        
//        RepositoryDocumentDAO repositoryDocumentDAO = (RepositoryDocumentDAO)ctx.getBean(RepositoryDocumentDAO.class);
//        Language l = repositoryDocumentDAO.getDocumentLanguage(89);
//        System.out.println("l = " + l.toString());
//        
//        
//        DeletedStatus amd = repositoryDocumentDAO.getDeletedStatus(89);
//        System.out.println("amd = " + amd);
//                
//        RepositoriesDAO repositoriesDAO = (RepositoriesDAO)ctx.getBean(RepositoriesDAO.class);
//        String name = repositoriesDAO.getRepositoryName(1);
//        System.out.println("name = " + name);
//        
//        CitationDAO citationDAO = (CitationDAO)ctx.getBean(CitationDAO.class);
//        List<Citation> citations = citationDAO.getCitations(1);
//        System.out.println("citations = " + citations);
//        
//        JournalsDAO journalsDAO = (JournalsDAO)ctx.getBean(JournalsDAO.class);
//        String title = journalsDAO.getJournalTitleByIdentifier("id1");
//        System.out.println("title = " + title);
//
//        repositoryDocumentDAO.setDocumentIndexStatus(89, 5);
//    }
//}
