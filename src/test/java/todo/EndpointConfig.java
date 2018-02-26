package todo;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.variable.GlobalVariables;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.xml.XsdSchemaRepository;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.xml.xsd.SimpleXsdSchema;

import java.util.Collections;

@Configuration
@PropertySource("citrus.properties")
public class EndpointConfig {

    @Bean
    public GlobalVariables globalVariables() {
        GlobalVariables variables = new GlobalVariables();
        variables.getVariables().put("project.name", "Citrus Integration Tests");
        return variables;
    }

    @Bean
    public HttpClient todoListClient() {
        return CitrusEndpoints.http()
                .client()
                .requestUrl("http://localhost:8080")
                .build();
    }


    @Bean
    public SimpleXsdSchema todoListSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schema/TodoList.xsd"));
    }

    @Bean
    public XsdSchemaRepository schemaRepository() {
        XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
        schemaRepository.getSchemas().add(todoListSchema());
        return schemaRepository;
    }

    @Bean
    public NamespaceContextBuilder namespaceContextBuilder() {
        NamespaceContextBuilder namespaceContextBuilder = new NamespaceContextBuilder();
        namespaceContextBuilder.setNamespaceMappings(Collections.singletonMap("todo", "http://citrusframework.org/samples/todolist"));
        return namespaceContextBuilder;
    }

    @Bean
    public SoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public WebServiceClient todoClient() {
        return CitrusEndpoints.soap()
                .client()
                .defaultUri("http://localhost:8080/services/ws/todolist")
                .build();
    }

}
