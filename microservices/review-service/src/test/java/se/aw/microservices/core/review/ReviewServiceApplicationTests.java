package se.aw.microservices.core.review;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.aw.api.core.review.Review;
import se.aw.microservices.core.review.services.ReviewServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewServiceApplicationTests {

	@Autowired
	WebTestClient testClient;


	@Test
	void contextLoads() {
	}

	@Test
	public void getReviewsTest(){
		testClient.get()
				.uri("/review?productId="+1)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Review.class).hasSize(3);

	}

	@Test
	public void  invalidInputTest(){
		testClient.get()
				.uri("/review?productId=0")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.SC_UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void  noReviewsTest(){
		testClient.get()
				.uri("/review?productId=213")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Review.class).hasSize(0);
	}



}
