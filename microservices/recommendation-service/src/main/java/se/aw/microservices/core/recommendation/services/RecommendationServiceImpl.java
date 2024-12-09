package se.aw.microservices.core.recommendation.services;

import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.aw.api.core.recommendation.Recommendation;
import se.aw.api.core.recommendation.RecommendationService;
import se.aw.microservices.core.recommendation.persistence.RecommendationEntity;
import se.aw.microservices.core.recommendation.persistence.RecommendationRepository;
import se.aw.util.exceptions.InvalidInputException;
import se.aw.util.http.ServiceUtil;

import static java.util.logging.Level.FINE;

@RestController
public class RecommendationServiceImpl implements RecommendationService {
    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final RecommendationRepository repository;

    private final RecommendationMapper mapper;


    @Autowired
    public RecommendationServiceImpl(ServiceUtil serviceUtil, RecommendationRepository repository, RecommendationMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository=repository;
        this.mapper=mapper;
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
        LOG.info("Will get recommendations for product with id={}", productId);


        return repository.findByProductId(productId).log(LOG.getName(), FINE).map(mapper::entityToApi).
                map(r->{
                    r.setServiceAddress(serviceUtil.getServiceAddress());
                    return r;
                });


//        map r->r.setServiceAddress(serviceUtil.getServiceAddress()); this will not return anything, so we give return
        // explicitly by writing a method.In reactive programming, transformations (map) must produce a value for the next stage in the reactive chain. If a map function returns void (or no value), the chain becomes invalid or produces unexpected results.
    }

    @Override
    public Mono<Recommendation> createRecommendation(Recommendation body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: " + body.getProductId());

        RecommendationEntity entity=mapper.apiToEntity(body);

        Mono<Recommendation> newEntity=repository.save(entity).log(LOG.getName(), FINE)
                .onErrorMap(DuplicateKeyException.class, ex->new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId()))
                .map(r->mapper.entityToApi(r));
        return newEntity;
    }

    @Override
    public Mono<Void> deleteRecommendations(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        return repository.deleteAll(repository.findByProductId(productId));

    }
}
