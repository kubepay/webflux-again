package com.kubepay.webflux.customer;


import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class CustomerControllerTest {
	
	Customer getCustomerAsPeron() {
		return Customer.builder().customerType(CustomerType.PERSON).build();
	}
	Customer getCustomerAsCompany() {
		return Customer.builder().customerType(CustomerType.COMPANY).build();
	}
	
	@Mock
	private CustomerRepository repo;

	@InjectMocks
	private CustomerController controller;

	@Test
	public void shouldReturnAllCustomers() {

		// Given
		final List<Customer> customers = asList(
				Customer.builder().customerType(CustomerType.PERSON).build(), 
				Customer.builder().customerType(CustomerType.COMPANY).build());
		when(repo.findAll()).thenReturn(Flux.fromIterable(customers));

		// When
		final ResponseEntity<List<Customer>> response = controller.allCustomers().block();

		// Then
		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat((Iterable<Customer>) response.getBody()).asList().containsAll(customers);
	}

	@Test
	public void shouldReturnEmptyBodyWhenNoCustomers() {

		// Given
		when(repo.findAll()).thenReturn(Flux.empty());

		// When
		final ResponseEntity<List<Customer>> response = controller.allCustomers().block();

		// Then
		assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
	}

	@Test
	public void shouldReturnOneCustomerById() {

		// Given
		final Customer customer = getCustomerAsPeron();
		when(repo.findById(any(ObjectId.class))).thenReturn(Mono.just(customer));

		// When
		final ResponseEntity<Customer> response = controller.oneCustomer(ObjectId.get()).block();

		// Then
		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat((Customer) response.getBody()).isEqualTo(customer);
	}

	@Test
	public void shouldReturn404IfCustomerIsNotFound() {

		// Given
		when(repo.findById(any(ObjectId.class))).thenReturn(Mono.empty());

		// When
		final ResponseEntity<Customer> response = controller.oneCustomer(ObjectId.get()).block();

		// Then
		assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
		assertThat(response.getBody()).isNull();
	}

	@Test
	public void shouldAddANewCustomer() {

		// Given
		final Customer newCustomer = getCustomerAsPeron();

		final ObjectId id = ObjectId.get();
		ReflectionTestUtils.setField(newCustomer, "id", id);

		when(repo.existsById(any(ObjectId.class))).thenReturn(Mono.just(false));
		when(repo.save(any(Customer.class))).thenReturn(Mono.just(newCustomer));

		// When
		final ResponseEntity<?> response = controller.addCustomer(newCustomer).block();

		// Then
		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		assertThat(response.getHeaders().getLocation().toString()).isEqualTo(format("/customers/%s", id));
	}

	@Test
	public void shouldNotAddACustomerIfCustomerAlreadyExists() throws Exception {

		// Given
		when(repo.existsById(any(ObjectId.class))).thenReturn(Mono.just(true));
		final ObjectId id = ObjectId.get();
		final Customer customer = getCustomerAsPeron();
		ReflectionTestUtils.setField(customer, "id", id);

		// When
		// Then
		assertThatThrownBy(() -> controller.addCustomer(customer).block())
			.isInstanceOf(CustomerServiceException.class)
			.hasMessageContaining("Customer already exists");
	}

	@Test
	public void shouldUpdateAnExistingCustomer() {

		// Given
		when(repo.existsById(any(ObjectId.class))).thenReturn(Mono.just(true));
		when(repo.save(any(Customer.class))).thenReturn(Mono.just(getCustomerAsPeron()));
		final ObjectId id = ObjectId.get();
		final Customer existingCustomer = getCustomerAsPeron();
		ReflectionTestUtils.setField(existingCustomer, "id", id);

		// When
		final ResponseEntity<?> response = controller.updateCustomer(id, existingCustomer).block();

		// Then
		assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
	}

	@Test
	public void shouldFailUpdatingNonExistingCustomer() {

		// Given
		when(repo.existsById(any(ObjectId.class))).thenReturn(Mono.just(false));
		final ObjectId id = ObjectId.get();
		final Customer newCustomer = getCustomerAsPeron();
		ReflectionTestUtils.setField(newCustomer, "id", id);

		// When
		// Then
		assertThatThrownBy(() -> controller.updateCustomer(newCustomer.getId(), newCustomer).block())
				.isInstanceOf(CustomerServiceException.class)
				.hasMessageContaining("Customer does not exist");
	}

	@Test
	public void shouldDeleteAnExistingCustomer() {

		// Given
		when(repo.existsById(any(ObjectId.class))).thenReturn(Mono.just(true));
		when(repo.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
		final ObjectId id = ObjectId.get();

		// When
		final ResponseEntity<?> response = controller.deleteCustomer(id).block();

		// Then
		assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
	}

	@Test
	public void shouldDeleteExistingCustomerAndIgnoreSubsequentCalls() throws Exception {

		// Given
		when(repo.existsById(any(ObjectId.class))).thenReturn(Mono.just(true)).thenReturn(Mono.just(false));
		when(repo.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
		final ObjectId id = ObjectId.get();

		// When
		final ResponseEntity<?> response1 = controller.deleteCustomer(id).block();
		final ResponseEntity<?> response2 = controller.deleteCustomer(id).block();
		final ResponseEntity<?> response3 = controller.deleteCustomer(id).block();

		// Then
		verify(repo).deleteById(any(ObjectId.class)); // Must be called only once
		assertThat(response1.getStatusCode()).isEqualTo(NO_CONTENT);
		assertThat(response2.getStatusCode()).isEqualTo(NO_CONTENT);
		assertThat(response3.getStatusCode()).isEqualTo(NO_CONTENT);
	}

}
