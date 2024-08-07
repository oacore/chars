package uk.ac.core.dataprovider.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.ac.core.dataprovider.api.controller.HomeController;
import static springfox.documentation.builders.PathSelectors.ant;
import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class DocumentationConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(basePackage("uk.ac.core.dataprovider.api.controller"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(org.joda.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.joda.time.DateTime.class, java.util.Date.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CORE API (Private)")
                .description("This is the API for CORE backend services. This API MUST NOT be directly exposed to the public without any authentication etc.")
                .license("No License Defined")
                .licenseUrl("https://core.ac.uk")
                .version("3.0")
                .contact(new Contact("", "", "theteam@core.ac.uk"))
                .build();
    }
}
