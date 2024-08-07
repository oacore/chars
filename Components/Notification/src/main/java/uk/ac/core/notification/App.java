//package uk.ac.core.notification;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import uk.ac.core.notification.api.NotificationService;
//
///**
// *
// * @author lucasanastasiou
// */
//@SpringBootApplication
//public class App implements CommandLineRunner{
//
//    @Autowired
//    NotificationService notificationService;
//    
//    public static void main(String[] args) {
//        SpringApplication.run(App.class, args);
//    }
//
//    @Override
//    public void run(String... strings) throws Exception {
//        notificationService.sendFinishHarvestingNotificationToWatchers(1);
//    }
//}
