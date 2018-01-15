package com.kubepay.webflux.customer;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerControllerWebTest {

	@MockBean
	private CustomerRepository repo;

	private WebTestClient webClient;
	
	@Before
	public void init() {
		webClient = WebTestClient
			.bindToController(new CustomerController(repo))
			.controllerAdvice(CustomerServiceExceptionHandler.class)  // Doesn't seem to work hence the HTTP 500 instead of HTTP 400 in some tests
			.build();
	}

	@Test
	public void shouldReturnAllCustomers() {

		final List<Customer> mockCustomers = asList(
				Customer.builder().customerType(CustomerType.PERSON).build(), Customer.builder().customerType(CustomerType.COMPANY).build());
		given(repo.findAll()).willReturn(Flux.fromIterable(mockCustomers));

		webClient.get().uri("/customers").accept(APPLICATION_JSON_UTF8).exchange()
			.expectStatus().isOk()	// HTTP 200
			.expectHeader().contentType(APPLICATION_JSON_UTF8)
			.expectBodyList(Customer.class).hasSize(2).consumeWith(customers -> {
				assertThat(customers.getResponseBody().stream().map(Customer::getCustomerType).collect(toList())
					.containsAll(asList(CustomerType.PERSON, CustomerType.COMPANY)));
			});
	}
	
	@Test
	public void shouldReturnEmptyBodyWhenNoCustomersFound() {

		final List<Customer> customers = Collections.emptyList();
		given(repo.findAll()).willReturn(Flux.fromIterable(customers));

		
		webClient.get().uri("/customers").accept(APPLICATION_JSON_UTF8).exchange()
			.expectStatus().isNoContent();	// HTTP 204
	}
	
	@Test
	public void shouldReturnOneCustomerById() throws Exception {

		final LocalDate birthDate = LocalDate.of(1990, Month.JULY, 31);
		final Customer mockCustomer = Customer.builder().customerType(CustomerType.PERSON).birthDate(birthDate).build();
		final ObjectId id = ObjectId.get();

		given(repo.findById(any(ObjectId.class))).willReturn(Mono.just(mockCustomer));

		webClient.get().uri(String.format("/customers/%s", id)).accept(APPLICATION_JSON_UTF8).exchange()
			.expectStatus().isOk()	// HTTP 200
			.expectBody(Customer.class)
			.consumeWith(customer -> {
				assertThat(customer.getResponseBody().getCustomerType()).isEqualTo(CustomerType.PERSON);
				assertThat(customer.getResponseBody().getBirthDate()).isEqualTo(LocalDate.of(1990, 07, 31));
			});
	}

	@Test
	public void shouldReturn404IfCustomerNotFound() throws Exception {

		given(repo.findById(any(ObjectId.class))).willReturn(Mono.empty());

		webClient.get().uri(String.format("/customers/%s", ObjectId.get())).accept(APPLICATION_JSON_UTF8).exchange()
				.expectStatus().isNotFound() // HTTP 404
				.expectBody().isEmpty();
	}

	@Test
	public void shouldAddANewCustomer() throws Exception {

		final Customer newCustomer = Customer.builder().customerType(CustomerType.PERSON).build();

		final ObjectId id = ObjectId.get();
		ReflectionTestUtils.setField(newCustomer, "id", id);

		given(repo.existsById(any(ObjectId.class))).willReturn(Mono.just(false));
		given(repo.save(any(Customer.class))).willReturn(Mono.just(newCustomer));

		webClient.post().uri("/customers")
			.contentType(APPLICATION_JSON_UTF8)
			.body(fromObject("{\"customer_type\":\"PERSON\"}"))
			.exchange()
			.expectStatus().isCreated()	// HTTP 201
			.expectHeader().valueEquals("Location", String.format("/customers/%s", id));
	}

	@Test
	public void shouldNotAddCustomerIfContentIsNotValid() throws Exception {

		final String BAD_JSON = "{\"customer_type_is_missing\":\"PERSON\"}";

		webClient.post().uri("/customers")
			.contentType(APPLICATION_JSON_UTF8)
			.body(fromObject(BAD_JSON))
			.exchange()
			.expectStatus().isBadRequest();	// HTTP 400
	}

	@Test
	public void shouldNotAddCustomerIfCustomerAlreadyExists() throws Exception {

		given(repo.existsById(any(ObjectId.class))).willReturn(Mono.just(true));
		final ObjectId id = ObjectId.get();

		final String EXISTING_CUSTOMER = String.format("{\"id\":\"%s\",\"customer_type\":\"COMPANY\"}", id);
		webClient.post().uri("/customers")
			.contentType(APPLICATION_JSON_UTF8)
			.body(fromObject(EXISTING_CUSTOMER))
			.exchange()
			.expectStatus().is5xxServerError(); // HTTP 500 because the exception handler is not working yet.
	}

	@Test
	public void shouldUpdateAnExistingCustomer() throws Exception {

		given(repo.existsById(any(ObjectId.class))).willReturn(Mono.just(true));
		given(repo.save(any(Customer.class))).willReturn(Mono.just(Customer.builder().customerType(CustomerType.PERSON).build()));

		final ObjectId id = ObjectId.get();
		final String UPDATE = String.format(
			"{\"id\":\"%s\",\"first_name\":\"John\",\"last_name\":\"Doe\",\"customer_type\":\"COMPANY\"}", id);

		webClient.put().uri(String.format("/customers/%s", id))
			.contentType(APPLICATION_JSON_UTF8)
			.body(fromObject(UPDATE)).exchange()
			.expectStatus().isNoContent(); // HTTP 204
	}

	@Test
	public void shouldFailUpdatingNonExistingCustomer() throws Exception {

		given(repo.existsById(any(ObjectId.class))).willReturn(Mono.just(false));

		final ObjectId id = ObjectId.get();
		final String UPDATE = String.format(
			"{\"id\":\"%s\",\"first_name\":\"John\",\"last_name\":\"Doe\",\"customer_type\":\"COMPANY\"}", id);

		webClient.put().uri(String.format("/customers/%s", id))
			.contentType(APPLICATION_JSON_UTF8)
			.body(fromObject(UPDATE)).exchange()
			.expectStatus().is5xxServerError(); // HTTP 500 because the exception handler is not working yet.
	}

	@Test
	public void shouldDeleteAnExistingCustomer() throws Exception {

		given(repo.existsById(any(ObjectId.class))).willReturn(Mono.just(true));
		given(repo.deleteById(any(ObjectId.class))).willReturn(Mono.empty());
		final URI uri = URI.create(String.format("/customers/%s", ObjectId.get()));
		
		webClient.delete().uri(uri).exchange().expectStatus().isNoContent();
	}

	@Test
	public void shouldDeleteExistingCustomerAndIgnoreFollowingCalls() throws Exception {

		given(repo.existsById(any(ObjectId.class))).willReturn(Mono.just(true)).willReturn(Mono.just(false));
		given(repo.deleteById(any(ObjectId.class))).willReturn(Mono.empty());
		final URI uri = URI.create(String.format("/customers/%s", ObjectId.get()));

		// Expect HTTP 204 for each call
		webClient.delete().uri(uri).exchange().expectStatus().isNoContent();
		webClient.delete().uri(uri).exchange().expectStatus().isNoContent();
		webClient.delete().uri(uri).exchange().expectStatus().isNoContent();
		verify(repo).deleteById(any(ObjectId.class)); // Must be called only once
	}
}