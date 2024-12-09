//package se.aw.microservices.core.product;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.dao.OptimisticLockingFailureException;
//import se.aw.microservices.core.product.persistence.ProductEntity;
//import se.aw.microservices.core.product.persistence.ProductRepository;
//
//import java.util.Optional;
//
//import static org.junit.Assert.assertEquals;
//
//
//@DataMongoTest
//public class PersistenceTest {
//
//    @Autowired
//    private ProductRepository repository;
//
//    private ProductEntity savedEntity;
//
//    @BeforeEach
//    void setupDb(){
//        repository.deleteAll();
//        ProductEntity entity = new ProductEntity(1,"n",1);
//        savedEntity=repository.save(entity);
//        assertEqualsProduct(entity,savedEntity);
//    }
//
//    @Test
//    public void create(){
//
//        ProductEntity newEntity= new ProductEntity(2,"n",2);
//        repository.save(newEntity);
//
//        ProductEntity foundEntity=repository.findById(newEntity.getId()).get();
//        assertEqualsProduct(newEntity,foundEntity);
//        Assertions.assertEquals(2,repository.count());
//
//
//    }
//
//    @Test
//    public void  updateTest(){
//        savedEntity.setName("n2");
//        repository.save(savedEntity);
//
//        ProductEntity entity=repository.findById(savedEntity.getId()).get();
//
//        Assertions.assertEquals(1,entity.getVersion());
//        Assertions.assertEquals("n2",entity.getName());
//    }
//
//    @Test
//    public void deleteTest(){
//        repository.delete(savedEntity);
//        Assertions.assertFalse(repository.existsById(savedEntity.getId()));
//
//    }
//
//    @Test
//    public void getByProductIdTest(){
//
//        Optional<ProductEntity> productEntity=repository.findByProductId(savedEntity.getProductId());
//        Assertions.assertTrue(productEntity.isPresent());
//        assertEqualsProduct(savedEntity,productEntity.get());
//
//    }
//
//
//    @Test
//    public void optimisticLockTest(){
//
//        //store entity in two separate objects
//        ProductEntity entity1=repository.findById(savedEntity.getId()).get();
//        ProductEntity entity2=repository.findById(savedEntity.getId()).get();
//
//        //update entity using first object
//        entity1.setName("n1");
//        repository.save(entity1);
//
//        //update the entity using second object
//        //This will fail since entity2 is stale(old version). Optimistic lock
//        try {
//            entity2.setName("n2");
//            repository.save(entity2);
//        }catch (OptimisticLockingFailureException e){}
//
//        // Check name of updated entity
//        ProductEntity updatedEntity = repository.findById(savedEntity.getId()).get();
//        assertEquals(1, (int)updatedEntity.getVersion());
//        assertEquals("n1", updatedEntity.getName());
//
//    }
//
//
//
//    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity){
//        Assertions.assertEquals(expectedEntity.getId(),actualEntity.getId());
//        Assertions.assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
//        Assertions.assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
//        Assertions.assertEquals(expectedEntity.getName(), actualEntity.getName());
//        Assertions.assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
//    }
//
//}
