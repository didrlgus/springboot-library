package gabia.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class SwaggerCommonConfig {

    private final ZuulProperties properties;

    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            List<SwaggerResource> resources = new ArrayList<>();
            properties.getRoutes().values().stream().filter(zuulRoute -> !zuulRoute.getServiceId().equals("auth-service"))
                    .forEach(route -> resources.add(createResource(route.getServiceId(), route.getId(), "2.0")));
            return resources;
        };
    }

    private SwaggerResource createResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation("/" + location + "/v2/api-docs");
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }

    public static final String[] SWAGGER_AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**",
            "/swagger-ui/**",
            "/user-service/v2/api-docs",
            "/book-service/v2/api-docs",
            "/review-service/v2/api-docs",
            "/alert-service/v2/api-docs",
            "/notice-service/v2/api-docs",
            "/book-request-service/v2/api-docs"
    };
}
