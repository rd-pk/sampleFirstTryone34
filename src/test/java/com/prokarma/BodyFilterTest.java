package com.prokarma;

import com.prokarma.rep.Person;
import com.prokarma.rep.PersonRepHelper;

import static org.assertj.core.api.Assertions.assertThat;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class BodyFilterTest {
    
    String resourceUrlPrefix;
    String validJson;
    String invalidJson;
    
    @Rule
    public final DropwizardAppRule<ProductServiceConfiguration> RULE =
        new DropwizardAppRule<ProductServiceConfiguration>(ProductService.class,
            ResourceHelpers.resourceFilePath("config.yml"));
    
    private Client client;

    @Before
    public void setup() throws Exception {
        client = new JerseyClientBuilder().build();
        resourceUrlPrefix = String.format("http://localhost:%d/filtered/FilteredWebResource", RULE.getLocalPort());
        validJson = new PersonRepHelper().serializesToJSON(new Person("Sri Hari", "stadepalli@prokarma.com"));
        invalidJson = "This is a invalid JSON payload for test.";
    }

    @After
    public void tearDown() {

    }
   
    @Test
    public void testValidJsonPaloadValidContentTypeHeaderToResource() throws Exception {
        Response responce = client.target(resourceUrlPrefix).request()
                .post(Entity.json(validJson));

        assertThat(responce.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testInvalidPaloadValidContentTypeHeaderToResource() throws Exception {
        Response responce = client.target(resourceUrlPrefix).request()
                .post(Entity.json(invalidJson));

        assertThat(responce.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
    }
    
    
    @Test
    public void testValidPaloadInvalidContentTypeHeaderToResource() throws Exception {
        Response responce = client.target(resourceUrlPrefix).request()
                .post(Entity.text(validJson));

        assertThat(responce.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

}
