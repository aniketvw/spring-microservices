//package se.aw.microservices.composite.product.apiconfig;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.annotation.RequestMethod;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//import java.util.Collections;
//
//import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
//
//
//@Configuration
//public class OasSpecifications {
//
//    @Value("${api.common.version}")           String apiVersion;
//    @Value("${api.common.title}")             String apiTitle;
//    @Value("${api.common.description}")       String apiDescription;
//    @Value("${api.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
//    @Value("${api.common.license}")           String apiLicense;
//    @Value("${api.common.licenseUrl}")        String apiLicenseUrl;
//    @Value("${api.common.contact.name}")      String apiContactName;
//    @Value("${api.common.contact.url}")       String apiContactUrl;
//    @Value("${api.common.contact.email}")     String apiContactEmail;
//
//
//    @Bean
//    public Docket apiDocumentation(){
//
//
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(basePackage("se.magnus.microservices.composite.product"))
//                .paths(PathSelectors.any())
//                .build()
//                .globalResponseMessage(RequestMethod.GET, Collections.emptyList())
//                .apiInfo(new ApiInfo(
//                        apiTitle,
//                        apiDescription,
//                        apiVersion,
//                        apiTermsOfServiceUrl,
//                        new Contact(apiContactName, apiContactUrl, apiContactEmail),
//                        apiLicense,
//                        apiLicenseUrl,
//                        Collections.emptyList()
//
//                ));
//
//    }
//
//
//}
