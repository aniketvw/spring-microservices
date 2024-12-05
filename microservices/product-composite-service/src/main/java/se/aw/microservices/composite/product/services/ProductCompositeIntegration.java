package se.aw.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.aw.api.core.product.Product;
import se.aw.api.core.product.ProductService;
import se.aw.api.core.recommendation.Recommendation;
import se.aw.api.core.recommendation.RecommendationService;
import se.aw.api.core.review.Review;
import se.aw.api.core.review.ReviewService;
import se.aw.util.exceptions.InvalidInputException;
import se.aw.util.exceptions.NotFoundException;
import se.aw.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.logging.Level;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static reactor.core.publisher.Flux.empty;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;
    private final WebClient webClient;

    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,

            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort,
            WebClient.Builder webClient
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.webClient=webClient.build();

        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }

    public Mono<Product> getProduct(int productId){

            String url=productServiceUrl+productId;
            LOG.debug("Will call getProduct API on URL: {}", url);

            return webClient.get().uri(url)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .log(LOG.getName(), Level.FINE)
                    .onErrorMap(WebClientResponseException.class,
                            ex->handleException(ex));

    }




    @Override
    public Product createProduct(Product body) {
        try{
            String url = productServiceUrl;
            LOG.info("Will post a new product to URL: {}", url);



            LOG.info("Created a product with id: {}", product.getProductId());

            return product;
        }catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call the deleteProduct API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private String getErrorMessage(WebClientResponseException ex){
        try {
            return mapper.readValue(ex.getResponseBodyAsString(),
                    HttpErrorInfo.class).getMessage();
        }catch (IOException ioex){
            return ex.getMessage();
        }
    }

    public Flux<Recommendation> getRecommendations(int productId) {


            String url = recommendationServiceUrl + productId;

            LOG.debug("Will call getRecommendations API on URL: {}", url);
            // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return  webClient.get().uri(url).retrieve().bodyToFlux(Recommendation.class).log()
                            .onErrorResume(error->empty());
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            String url = recommendationServiceUrl;
            LOG.debug("Will post a new recommendation to URL: {}", url);

            Recommendation recommendation = restTemplate.postForObject(url, body, Recommendation.class);
            LOG.debug("Created a recommendation with id: {}", recommendation.getProductId());

            return recommendation;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public Flux<Review> getReviews(int productId) {

            String url = reviewServiceUrl + productId;

            LOG.debug("Will call getReviews API on URL: {}", url);
            return  webClient.get().uri(url).retrieve()
                    .bodyToFlux(Review.class).log()
                    .onErrorResume(error->empty());
    }

    @Override
    public Review createReview(Review body) {

        try {
            String url = reviewServiceUrl;
            LOG.debug("Will post a new review to URL: {}", url);

            Review review = restTemplate.postForObject(url, body, Review.class);
            LOG.debug("Created a review with id: {}", review.getProductId());

            return review;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteReviews API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }


    private Throwable handleException(Throwable ex) {

        if(!(ex instanceof WebClientResponseException))
        {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException exception= (WebClientResponseException) ex;

        HttpStatusCode statusCode = exception.getStatusCode();
        if (statusCode.equals(NOT_FOUND)) {
            return new NotFoundException(getErrorMessage(exception));
        } else if (statusCode.equals(UNPROCESSABLE_ENTITY)) {
            return new InvalidInputException(getErrorMessage(exception));
        }
        LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", exception.getStatusCode());
        LOG.warn("Error body: {}", exception.getResponseBodyAsString());
        return ex;
    }



}
