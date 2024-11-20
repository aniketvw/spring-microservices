package se.aw.microservices.core.product.services;

import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.aw.api.core.product.Product;
import se.aw.api.core.product.ProductService;
import se.aw.microservices.core.product.persistence.ProductEntity;
import se.aw.microservices.core.product.persistence.ProductRepository;
import se.aw.util.exceptions.InvalidInputException;
import se.aw.util.exceptions.NotFoundException;
import se.aw.util.http.ServiceUtil;

@RestController
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final ProductRepository repository;

    private final ProductMapper mapper;

    @Autowired
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper,ServiceUtil serviceUtil){
        this.serviceUtil=serviceUtil;
        this.repository=repository;
        this.mapper=mapper;
    }


    @Override
    public Product getProduct(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId:" + productId);

        ProductEntity entity= repository.findByProductId(productId)
                .orElseThrow(()-> new NotFoundException("No product found for productId: "+ productId));

        Product response= mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getProduct: found productId: {}", response.getProductId());

        return response;
    }

    @Override
    public Product createProduct(Product body) {
        try{

                ProductEntity entity = mapper.apiToEntity(body);
                ProductEntity newEntity= repository.save(entity);

                LOG.debug("createProduct: entity created for proudctId: {}",body.getProductId());
                return mapper.entityToApi(newEntity);

        }catch (DuplicateKeyException dke){
            throw new InvalidInputException("Duplicate key, Product Id: "+body.getProductId());
        }
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        repository.findByProductId(productId).ifPresent(e->repository.delete(e));
    }
}

