package com.kubepay.webflux.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;




/**
 * Top to bottom integration test.
 * <p>
 * The application server (specified in pom.xml) is started and the service
 * deployed.
 * <p>
 * This class tests all CRUD operations in one big test.
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

//@ContextConfiguration(classes = {SecurityConfig.class})
public class CustomerServiceTest {
	


	@LocalServerPort
	private int port;
	


	@Test
	public void testCRUDOperationsAllTogether() throws IOException {
		
		final WebClient webClient = WebClient.builder().baseUrl(String.format("http://127.0.0.1:%d", port)).build();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(ACCEPT, APPLICATION_JSON_UTF8_VALUE);
		httpHeaders.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);

		final Customer newCustomer = Customer.builder().customerType(CustomerType.PERSON)
				.birthDate(LocalDate.of(1990, Month.AUGUST, 16)).build();

		// ---------- Create ----------
		ClientResponse resp = webClient.post().uri("/customers").headers(headers -> headers.addAll(httpHeaders))
				.body(fromObject(newCustomer)).exchange().block();

		assertThat(resp.statusCode()).isEqualTo(CREATED);
		final String newCustomerUrl = resp.headers().header("Location").get(0);
		assertThat(newCustomerUrl).contains("/customers/");

		// ---------- Read ----------
		final Customer createdCustomer = webClient.get().uri(newCustomerUrl)
				.headers(headers -> headers.addAll(httpHeaders)).retrieve().bodyToMono(Customer.class).block();

		assertThat(createdCustomer.getId()).isNotNull();

		// ---------- Update ----------
		final Customer customerToUpdate = Customer.builder().customerType(CustomerType.PERSON)
				.birthDate(LocalDate.of(1990, Month.AUGUST, 16)).firstName("John").lastName("Doe")
				.id(createdCustomer.getId()).build();
		;
		resp = webClient.put().uri(newCustomerUrl).headers(headers -> headers.addAll(httpHeaders))
				.body(fromObject(customerToUpdate)).exchange().block();

		assertThat(resp.statusCode()).isEqualTo(NO_CONTENT);
		resp = webClient.get().uri(newCustomerUrl).headers(headers -> headers.addAll(httpHeaders)).exchange().block();
		assertThat(resp.statusCode()).isEqualTo(OK);
		final Customer updatedCustomer = resp.bodyToMono(Customer.class).block();
		assertThat(updatedCustomer.getFirstName()).isEqualTo("John");
		assertThat(updatedCustomer.getLastName()).isEqualTo("Doe");

		// ---------- Delete ----------
		resp = webClient.delete().uri(newCustomerUrl).headers(headers -> headers.addAll(httpHeaders)).exchange()
				.block();
		assertThat(resp.statusCode()).isEqualTo(NO_CONTENT);

		resp = webClient.get().uri(newCustomerUrl).headers(headers -> headers.addAll(httpHeaders)).exchange().block();
		assertThat(resp.statusCode()).isEqualTo(NOT_FOUND);
	}

}
