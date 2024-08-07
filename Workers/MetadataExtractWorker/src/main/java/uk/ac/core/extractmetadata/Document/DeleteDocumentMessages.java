package uk.ac.core.extractmetadata.Document;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;

@Service
public class DeleteDocumentMessages {

    static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public DeleteDocumentMessages(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void submit(SingleItemTaskParameters singleItemTaskParameters) {
        this.rabbitTemplate.convertAndSend("core-tasks-exchange", "purge-document", singleItemTaskParameters);
    }

}
