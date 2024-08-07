package uk.ac.core.worker.sitemap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.worker.sitemap.collection.ESDumpDeserializer;
import uk.ac.core.worker.sitemap.collection.ESDumpDeserializerImpl;
import uk.ac.core.worker.sitemap.generation.SitemapGenerator;
import uk.ac.core.worker.sitemap.generation.SitemapGeneratorImpl;
import uk.ac.core.worker.sitemap.service.SitemapService;
import uk.ac.core.worker.sitemap.service.SitemapServiceImpl;

@Configuration
public class SitemapConfig {

    @Bean
    public SitemapGenerator sitemapGenerator() {
        return new SitemapGeneratorImpl(xmlMapper());
    }

    @Bean
    public SitemapService sitemapService() {
        return new SitemapServiceImpl(esDumpDeserializer(), sitemapGenerator());
    }

    @Bean
    public ESDumpDeserializer esDumpDeserializer() {
        return new ESDumpDeserializerImpl(new ObjectMapper().
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return xmlMapper;
    }
}