package com.example.swaggertest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.ServletContextAware;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ImplicitGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@PropertySource({"classpath:application.properties"})
@EnableSwagger2
public class SwaggerConfig implements ServletContextAware {
    private static final String SECURITY_SCHEMA_OAUTH2 = "oauth2";
    private ServletContext servletContext;

    @Value("${springfox.documentation.swagger.v2.host}")
    private String hostName;

    @Value("${swagger.ui.oauth2.authorize.url}")
    private String authorizeUrl;

    @Value("${swagger.ui.oauth2.clientId}")
    private String swaggerAppClientId;

    @Value("${swagger.ui.oauth2.clientSecret}")
    private String swaggerClientSecret;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathProvider(pathProvider())
                .host(hostName)
                .select()
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(newArrayList(oauth()));
    }

    @Bean
    public SecurityScheme oauth() {
        return new OAuthBuilder()
                .name(SECURITY_SCHEMA_OAUTH2)
                .grantTypes(grantTypes())
                .scopes(scopes())
                .build();
    }

    @Bean
    public SecurityConfiguration securityInfo() {
        return new SecurityConfiguration(swaggerAppClientId, swaggerClientSecret, "realm", "swagger", "", ApiKeyVehicle.HEADER, "", " ");
    }

    private List<AuthorizationScope> scopes() {
        return newArrayList(
                new AuthorizationScope("write", "write and read"),
                new AuthorizationScope("read", "read only"));
    }

    private List<GrantType> grantTypes() {
        GrantType grantType = new ImplicitGrantBuilder()
                .loginEndpoint(new LoginEndpoint(authorizeUrl))
                .tokenName("access_token")
                .build();

        return newArrayList(grantType);
    }

    private PathProvider pathProvider() {
        return new RelativePathProvider(servletContext) {
            @Override
            protected String applicationPath() {
                return "/testAPI/v2/agent";
            }
        };
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Test API")
                .description("The Test API ")
                .version("1.0")
                .build();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}


