package com.prokarma;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.zipkin.ConsoleZipkinFactory;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Dropwizard based application's configuration.
 */
public class ProductServiceConfiguration extends Configuration {
    @NotNull
    @Valid
    public final ConsulFactory consul = new ConsulFactory();
	@Valid
    @NotNull
    public final ZipkinFactory zipkin = new ConsoleZipkinFactory();
    
    @Valid
    @NotNull
    private final ZipkinClientConfiguration zipkinClient = new ZipkinClientConfiguration();
    @JsonProperty
    public ConsulFactory getConsulFactory() {
        return consul;
    }
	@JsonProperty
    public ZipkinFactory getZipkinFactory() {
        return zipkin;
    }

    @JsonProperty
    public ZipkinClientConfiguration getZipkinClient() {
        return zipkinClient;
    }
}
