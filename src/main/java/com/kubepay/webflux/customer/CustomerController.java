package com.kubepay.webflux.customer;

import static java.lang.String.format;
import static org.springframework.http.MediaType.*;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/customers", produces = { APPLICATION_JSON_UTF8_VALUE })
public class CustomerController {

	private CustomerRepository repo;

	public CustomerController(CustomerRepository repo) {
		this.repo = repo;
	}

	/**
	 * Query for all customers.
	 * <p>
	 * This method is idempotent.
	 * 
	 * @return HTTP 200 if customers found or HTTP 204 otherwise.
	 */
	//@PreAuthorize("#oauth2.hasAnyScope('read','write','read-write')")
	@RequestMapping(method = GET)
	public Mono<ResponseEntity<List<Customer>>> allCustomers() {

		return repo.findAll().collectList()
			.filter(customers -> customers.size() > 0)
			.map(customers -> ok(customers))
			.defaultIfEmpty(noContent().build());
	}

	/**
	 * Query for a customer with the given Id.
	 * <p>
	 * This method is idempotent.
	 * 
	 * @param id
	 *            The id of the customer to look for.
	 * 
	 * @return HTTP 200 if the customer is found or HTTP 404 otherwise.
	 */
	//@PreAuthorize("#oauth2.hasAnyScope('read','write','read-write')")
	@RequestMapping(method = GET, value = "/{id}")
	public Mono<ResponseEntity<Customer>> oneCustomer(@PathVariable @NotNull ObjectId id) {

		return repo.findById(id)
			.map(customer -> ok().contentType(APPLICATION_JSON_UTF8).body(customer))
			.defaultIfEmpty(notFound().build());
	}

	/**
	 * Create a new customer.
	 * 
	 * @param newCustomer
	 *            The customer to create.
	 * 
	 * @return HTTP 201, the header Location contains the URL of the created
	 *         customer.
	 */
	//@PreAuthorize("#oauth2.hasAnyScope('write','read-write')")
	@RequestMapping(method = POST, consumes = { APPLICATION_JSON_UTF8_VALUE })
	public Mono<ResponseEntity<?>> addCustomer(@RequestBody @Valid Customer newCustomer) {

		return Mono.justOrEmpty(newCustomer.getId())
			.flatMap(id -> repo.existsById(id))
			.defaultIfEmpty(Boolean.FALSE)
			.flatMap(exists -> {

				if (exists) {
					throw new CustomerServiceException(HttpStatus.BAD_REQUEST,
						"Customer already exists, to update an existing customer use PUT instead.");
				}

				return repo.save(newCustomer).map(saved -> {
					return created(URI.create(format("/customers/%s", saved.getId()))).build();
				});
			});
	}

	/**
	 * Update an existing customer.
	 * <p>
	 * This method is idempotent.
	 * <p>
	 * 
	 * @param id
	 *            The id of the customer to update.
	 * @param customerToUpdate
	 *            The Customer object containing the updated version to be
	 *            persisted.
	 * 
	 * @return HTTP 204 otherwise HTTP 400 if the customer does not exist.
	 */
	//@PreAuthorize("#oauth2.hasAnyScope('write','read-write')")
	@RequestMapping(method = PUT, value = "/{id}", consumes = { APPLICATION_JSON_UTF8_VALUE })
	public Mono<ResponseEntity<?>> updateCustomer(@PathVariable @NotNull ObjectId id,
			@RequestBody @Valid Customer customerToUpdate) {

		return repo.existsById(id).flatMap(exists -> {

			if (!exists) {
				throw new CustomerServiceException(HttpStatus.BAD_REQUEST,
					"Customer does not exist, to create a new customer use POST instead.");
			}

			return repo.save(customerToUpdate).then(Mono.just(noContent().build()));
		});
	}

	/**
	 * Delete a customer.
	 * <p>
	 * This method is idempotent, if it's called multiples times with the same
	 * id then the first call will delete the customer and subsequent calls will
	 * be silently ignored.
	 * 
	 * @param id
	 *            The id of the customer to delete.
	 * @return HTTP 204
	 */
	//@PreAuthorize("#oauth2.hasAnyScope('write','read-write')")
	@RequestMapping(method = DELETE, value = "/{id}")
	public Mono<ResponseEntity<?>> deleteCustomer(@PathVariable @NotNull ObjectId id) {

		final Mono<ResponseEntity<?>> noContent = Mono.just(noContent().build());

		return repo.existsById(id)
			.filter(Boolean::valueOf) // Delete only if customer exists
			.flatMap(exists -> repo.deleteById(id).then(noContent))
			.switchIfEmpty(noContent);
	}
}
