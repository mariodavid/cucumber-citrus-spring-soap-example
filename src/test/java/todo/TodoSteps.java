package todo;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.validation.xml.XmlMarshallingValidationCallback;
import com.consol.citrus.ws.client.WebServiceClient;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.citrusframework.samples.todolist.GetTodoListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;


public class TodoSteps {

    @CitrusResource
    private TestDesigner designer;


    @Autowired
    private WebServiceClient todoClient;


    @When("^I send a TODO \"([^\"]*)\" to the server$")
    public void iSendATODOToTheServer(String todoName) throws Throwable {
        designer.variable("todoName", todoName);
        designer.variable("todoDescription", "Description: ${todoName}");

        designer.soap()
                .client(todoClient)
                .send()
                .soapAction("addTodoEntry")
                .payload(new ClassPathResource("templates/addTodoEntryRequest.xml"));


        designer.soap()
                .client(todoClient)
                .receive()
                .payload(new ClassPathResource("templates/addTodoEntryResponse.xml"));

    }



    @Then("^the TODO \"([^\"]*)\" was created$")
    public void theTODOWasCreated(String todoName) throws Throwable {

        designer.variable("todoName", todoName);

        designer.soap()
                .client(todoClient)
                .send()
                .soapAction("getTodoList")
                .payload(new ClassPathResource("templates/getTodoListRequest.xml"));

        designer.soap()
                .client(todoClient)
                .receive()

                // validation can be executed by an explicit groovy script
                .validateScript(new ClassPathResource("templates/getTodoListResponseValidator.groovy"))

                // or by a callback function which
                .validationCallback(new XmlMarshallingValidationCallback<GetTodoListResponse>() {
                    @Override
                    public void validate(GetTodoListResponse payload, Map<String, Object> headers, TestContext context) {

                        int todoEntryCount = payload.getList().getTodoEntry().size();

                        assertThat(todoEntryCount).isGreaterThan(100);
                    }
                });
    }

}
