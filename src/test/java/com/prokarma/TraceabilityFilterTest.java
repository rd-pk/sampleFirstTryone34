package com.prokarma;

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

public class TraceabilityFilterTest {
    
    private static String TRACEABILITY_ID = "trace-id";
    String resourceUrlPrefix;
    
    @Rule
    public final DropwizardAppRule<ProductServiceConfiguration> RULE =
        new DropwizardAppRule<ProductServiceConfiguration>(ProductService.class,
            ResourceHelpers.resourceFilePath("config.yml"));
    
    private Client client;

    @Before
    public void setup() throws Exception {
        client = new JerseyClientBuilder().build();
        resourceUrlPrefix = String.format("http://localhost:%d/filtered/FilteredWebResource", RULE.getLocalPort());
    }

    @After
    public void tearDown() {

    }
   
    @Test
    public void testTraceabilityIdSetToOne() throws Exception {
        Response responce = client.target(resourceUrlPrefix).request().header(TRACEABILITY_ID, 1)
                .get();
        
        String trace_id = responce.getHeaderString(TRACEABILITY_ID) ;

        assertThat(trace_id).isNotNull();
    }
    
    @Test
    public void testTraceabilityIdNotSet() throws Exception {
        Response responce = client.target(resourceUrlPrefix).request().get();
        
        String trace_id = responce.getHeaderString(TRACEABILITY_ID) ;

        assertThat(trace_id).isNull();
    }

}
