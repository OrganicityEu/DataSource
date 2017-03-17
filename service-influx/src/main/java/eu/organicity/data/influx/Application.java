package eu.organicity.data.influx;

import com.google.common.base.Predicate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"eu.organicity.data"})
@ComponentScan({"eu.organicity.data.*"})
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket annotationApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("DataSource-API").apiInfo(apiInfo()).select().paths(paths()).build();
    }

    private Predicate<String> paths() {
        return or(regex("/.*"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Organicity DataSource API").description("Organicity DataSource API").termsOfServiceUrl("http://www.organicity.eu").version("1.0-SNAPSHOT").build();
    }
}
