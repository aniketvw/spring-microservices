package se.aw.microservices.core.recommendation.services;

import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.aw.api.core.recommendation.Recommendation;
import se.aw.api.core.recommendation.RecommendationService;
import se.aw.microservices.core.recommendation.persistence.RecommendationEntity;
import se.aw.microservices.core.recommendation.persistence.RecommendationRepository;
import se.aw.util.exceptions.InvalidInputException;
import se.aw.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

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
    public List<Recommendation> getRecommendations(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<RecommendationEntity> entityList = repository.findByProductId(productId);
        List<Recommendation> list= mapper.entityToApiList(entityList);
        list.forEach(e-> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRecommendations: response size: {}", list.size());

        return list;
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            RecommendationEntity entity=mapper.apiToEntity(body);
            RecommendationEntity newEntity=repository.save(entity);

            LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
            return  mapper.entityToApi(newEntity);


        }catch (DuplicateKeyException dke){
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
