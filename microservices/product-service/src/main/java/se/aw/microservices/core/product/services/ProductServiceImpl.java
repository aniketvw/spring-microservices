package se.aw.microservices.core.product.services;

import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
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
    public Mono getProduct(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId:" + productId);

        return repository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " +productId)))
                .log()
                .map(e->mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});

    }

    @Override
    public Product createProduct(Product body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: " + body.getProductId());

        ProductEntity entity=mapper.apiToEntity(body);
        Mono<Product> newEntity=repository.save(entity)
                .log()
                .onErrorMap(DuplicateKeyException.class,
                        ex->new InvalidInputException("Duplicate key, ProductId:"+body.getProductId()))
                .map(e->mapper.entityToApi(e));
        return newEntity.block();


    }

    @Override
    public void deleteProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);

        repository.findByProductId(productId).map(repository::delete).flatMap(p->p).block();

        //        repository.findByProductId(productId).log().map(repository::delete).block();
        //1. repository.findByProductId(productId).log().map(repository::delete).block();
        //Steps in this code:
        //
        //repository.findByProductId(productId): Finds an entity by productId, returning a Mono<Product>.
        //.log(): Logs the events in the reactive chain for debugging purposes.
        //.map(repository::delete): Transforms the Product object into a Mono<Void> by applying the repository.delete() method.
        //.block(): Blocks the reactive chain and waits for the result.
        //Key point:
        //
        //The map operator wraps the result of repository.delete() into a Mono<Mono<Void>>. Since block() is directly called on the outer chain, it does not wait for the inner Mono<Void> to complete. This means the delete operation might not actually execute as expected or might execute asynchronously without guaranteeing the block will wait.
        //2. repository.findByProductId(productId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();
        //Steps in this code:
        //
        //repository.findByProductId(productId): Finds an entity by productId, returning a Mono<Product>.
        //.log(): Logs the events in the reactive chain.
        //.map(e -> repository.delete(e)): Maps the Product object to a Mono<Void>, similar to the first approach.
        //.flatMap(e -> e): Flattens the Mono<Mono<Void>> into a single Mono<Void>.
        //.block(): Blocks the reactive chain and waits for the inner Mono<Void> to complete, ensuring the delete operation is executed.
        //Key point:
        //
        //Using flatMap ensures the delete operation is executed and awaited properly. The chain will wait for the Mono<Void> returned by repository.delete() to complete before proceeding.
        //Always use the second approach (map + flatMap) when working with reactive streams that return another Mono or Flux. This ensures proper flattening and execution of asynchronous operations.
    }

}

