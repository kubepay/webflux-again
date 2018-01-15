package com.kubepay.webflux.stockquote;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.runner.RunWith;

//import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockQuotesApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	// curl http://localhost:8081/quotes -i -H "Accept: application/stream+json"
	@Test
	public void fetchQuotes() {
		webTestClient.get().uri("/quotes").accept(MediaType.APPLICATION_STREAM_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON);
		// .expectBodyList(Quote.class);//.hasSize(20);
		// .consumeWith(allQoutes -> {
		// assertThat(allQoutes.getResponseBody()).allSatisfy(quote ->
		// assertThat(quote.getPrice()).isPositive());
		// });

	}

	// curl http://localhost:8081/quotes -i -H "Accept: application/json"
	@Test
	public void fetch20Quotes() {
		webTestClient.get().uri("/quotes?size=20").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(Quote.class).hasSize(20)
				.consumeWith(allQoutes -> {
					assertThat(allQoutes.getResponseBody())
							.allSatisfy(quote -> assertThat(quote.getPrice()).isPositive());
				});

	}

	// curl http://localhost:8081/hello -i
	@Test
	public void hello() {
		webTestClient.get().uri("/hello").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.TEXT_PLAIN).expectBody(String.class).isEqualTo("Hello Spring!");
	}

	// curl http://localhost:8081/echo -i -d "WebFlux workshop" -H "Content-Type:
	// text/plain"
	@Test
	public void echo() {
		webTestClient.post().uri("/echo").accept(MediaType.TEXT_PLAIN).body(Mono.just("WebFlux workshop"), String.class)
				.exchange().expectStatus().isOk().expectHeader().contentType(MediaType.TEXT_PLAIN)
				.expectBody(String.class).isEqualTo("WebFlux workshop");
		// .consumeWith(body -> {
		// System.out.println("BODY: -------------->"+body);
		// assertThat(body).isEqualTo("WebFlux workshop").isNotNull();
		// });
	}

}