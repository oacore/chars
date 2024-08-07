package uk.ac.core.notification.services.email;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String templateName, Map<String, String> vars) {
        Context context = new Context();
        for (String key : vars.keySet()) {
            context.setVariable(key,vars.get(key));
        }
        return templateEngine.process(templateName, context);
    }

}
