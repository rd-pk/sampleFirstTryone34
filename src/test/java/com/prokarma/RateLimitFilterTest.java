package com.prokarma;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import javax.ws.rs.client.Client;


//
// Note: These tests excercise the running Application to verify RateLimit feature is working
// 		 Please make sure the application is built with below maven command 
//       mvn -DskipTests=true clean install
//       and start the application with below command before running these tests.
//       java <application.jar> server config.xml
//

public class RateLimitFilterTest {
    
    
    @Rule
    public final DropwizardAppRule<ProductServiceConfiguration> RULE =
        new DropwizardAppRule<ProductServiceConfiguration>(ProductService.class,
            ResourceHelpers.resourceFilePath("config.yml"));

    
    
    private Client client;

    @Before
    public void setup() {
        client = new JerseyClientBuilder().build();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetSingleFilteredTextResource() {

        final String resourceUrlPrefix = String.format("http://localhost:%d/filtered/FilteredWebResource", RULE.getLocalPort());
        String clientId = String.format("Client-Id-%d", new Random().nextInt(200)+1);
        String resp = client.target(resourceUrlPrefix).request().header("Client-Id", clientId).header("Accept", "text/plain").get().readEntity(String.class);
        String expected = "GET request on filtered resource named:FilteredWebResource";
        // Ignore the line ending characters, depending on OS line ending chars at and of response change.
        assertTrue(resp.contains(expected));
    }

    @Test
    public void testGetMultiFilteredTextResource() {

        final String resourceUrlPrefix = String.format("http://localhost:%d/filtered/FilteredWebResource", RULE.getLocalPort());
        //final int status_before_limit = 200;
        final int status_after_limit = 429;
        int responce = 0;
        String clientId = String.format("Client-Id-%d", new Random().nextInt(200)+1);
        System.out.println(">> Client-Id:" + clientId);
        for (int i = 1; i !=15; i++) {
            String resource = String.format("%s-%d", resourceUrlPrefix, i);
            responce = client.target(resource).request().header("Client-Id", clientId).get().getStatus();
            
            if (i > 10)
                assertEquals(responce, status_after_limit);
            // We can't assert on the status before 10 requests, as we don't know the status of server.
            //else
               //assertEquals(responce, status_before_limit);                
        }
    }

}
