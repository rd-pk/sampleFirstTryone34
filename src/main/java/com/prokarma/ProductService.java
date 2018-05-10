package com.prokarma;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import io.dropwizard.Application;
import com.prokarma.basicauth.AppAuthenticator;
import com.prokarma.basicauth.AppAuthorizer;
import com.prokarma.basicauth.BasicAuthUser;
import com.prokarma.resources.EmployeeResource;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import com.prokarma.pkmst.filter.HttpServletFilterHandler;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
import com.prokarma.pkmst.filter.AuditFilterHandler;
import com.prokarma.pkmst.filter.RateLimitFilterHandler;
import com.prokarma.pkmst.filter.BodyFilterHandler;
import com.prokarma.pkmst.filter.TraceabilityFilterHandler;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.io.IOException;
import com.smoketurner.dropwizard.consul.ConsulBundle;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import brave.http.HttpTracing;
import java.util.Optional;
import javax.ws.rs.client.Client;
import java.util.function.Consumer;

/**
 * Dropwizard based application launcher.
 */
public class ProductService extends Application<ProductServiceConfiguration> {

    /**
     * Java entry point.
     * 
     * @param args the command-line arguments.
     * @throws Exception an error occurred.
     */
    public static void main(String[] args) throws Exception {
        new ProductService().run(args);
    }

    /**
     * @see io.dropwizard.Application#initialize(io.dropwizard.setup.Bootstrap)
     */
    @Override
    public void initialize(Bootstrap<ProductServiceConfiguration> bootstrap) {
    
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
         bootstrap.addBundle(new ConsulBundle<ProductServiceConfiguration>(getName()) {
            @Override
            public ConsulFactory getConsulFactory(ProductServiceConfiguration configuration) {
                return configuration.getConsulFactory();
            }
        });
		 bootstrap.addBundle(new ZipkinBundle<ProductServiceConfiguration>(getName()) {
            @Override
            public ZipkinFactory getZipkinFactory(ProductServiceConfiguration configuration) {
                return configuration.getZipkinFactory();
            }
        });
        bootstrap.addBundle(new AssetsBundle());
    }

    /**
     * @see io.dropwizard.Application#run(io.dropwizard.Configuration, io.dropwizard.setup.Environment)
     */
    @Override
    public void run(ProductServiceConfiguration configuration, Environment environment) {
		environment.jersey().register(new EmployeeResource(environment.getValidator()));
		//Setup Basic Security
		environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<BasicAuthUser>()
                .setAuthenticator(new AppAuthenticator())
                .setAuthorizer(new AppAuthorizer())
                .setRealm("App Security")
                .buildAuthFilter()));
      	environment.jersey().register(new AuthValueFactoryProvider.Binder<>(BasicAuthUser.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);    
		Optional<HttpTracing> tracing = configuration.getZipkinFactory().build(environment);
        final Client client;
        if (tracing.isPresent()) {
            client = new ZipkinClientBuilder(environment, tracing.get())
                    .build(configuration.getZipkinClient());
        } else {
            final ZipkinClientConfiguration clientConfig = configuration
                    .getZipkinClient();
            client = new JerseyClientBuilder(environment).using(clientConfig)
                    .build(clientConfig.getServiceName());
        }
        // Common modules
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                // Configuration
                bind(configuration).to(ProductServiceConfiguration.class);
            }
        });

        // Resources
        registerModules("com.prokarma.resources", "Resource", (classInfo) -> {
            environment.jersey().register(classInfo.load());
        });


        final boolean isMatchAfter = true;
        // Register Servlet filters.

        environment.servlets().addFilter("AuditFilter", new HttpServletFilterHandler(new AuditFilterHandler())).
        addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), isMatchAfter, "/filtered/*");

        environment.servlets().addFilter("RateLimitFilter", new HttpServletFilterHandler(new RateLimitFilterHandler())).
        addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), isMatchAfter, "/filtered/*");

        environment.servlets().addFilter("BodyFilter", new HttpServletFilterHandler(new BodyFilterHandler())).
        addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), isMatchAfter, "/filtered/*");

        environment.servlets().addFilter("TraceabilityFilter", new HttpServletFilterHandler(new TraceabilityFilterHandler())).
        addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), isMatchAfter, "/filtered/*");

    }


    private void registerModules(String packageName, String classNameSuffix, Consumer<? super ClassInfo> action) {
        try {
            ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive(packageName)
                    .stream().filter((classInfo) -> classInfo.getName().endsWith(classNameSuffix)).forEach(action);
        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
    


}
