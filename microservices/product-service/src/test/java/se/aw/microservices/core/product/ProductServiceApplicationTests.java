package se.aw.microservices.core.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.aw.api.core.product.Product;
import se.aw.microservices.core.product.services.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@Autowired
	ProductServiceImpl productService;

	@Autowired
	WebTestClient client;

	@Test
	void contextLoads() {
	}

	@Test
	public void getProductTest(){

		client.get().uri("/product/1")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody().jsonPath("$.productId").isEqualTo(1);


	}

	@Test
	public void invalidInputTest(){
		client.get()
				.uri("/product/abc")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody().jsonPath("$.path").isEqualTo("/product/abc")
				.jsonPath("$.message").isEqualTo("Type mismatch.");

	}


}
