package uk.ac.core.queue;

import java.util.List;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.core.common.model.task.TaskDescription;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication
public class QuickTest implements CommandLineRunner {

    @Autowired
    QueueInfoService infoService;

    @Autowired
    QueueService qService;

//    @Autowired
//    AnnotationConfigApplicationContext context;

    public static void main(String... args) {
        SpringApplication.run(QuickTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        String queueName = "harvest-queue";
//        List<Message> msgs = infoService.lookMessagesInQueue(queueName);
//
//        for (Message msg : msgs) {
//            System.out.println("msg = " + msg);
//        }
//
//        System.out.println("Currently in queue:\t"+infoService.getCountMessages(queueName));
//        
//        TaskDescription taskDescription = new TaskDescription();
//        taskDescription.setRoutingKey("harvest");
//
//        qService.publish(taskDescription);
//
//        System.out.println("Currently in queue:\t"+infoService.getCountMessages(queueName));

//        context.close();
    }

}
