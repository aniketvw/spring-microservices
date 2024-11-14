package se.aw.microservices.core.product.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.aw.api.core.product.Product;
import se.aw.microservices.core.product.persistence.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "serviceAddress", ignore = true)
    Product entityToApi(ProductEntity entity);

    //ID and version are being ignored from incoming API request in create API as mongo takes care of it
    //
    @Mappings({@Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)})
    ProductEntity apiToEntity(Product api);

}
