package com.kubepay.webflux.customer;



import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Integration tests using RestAssured API
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerServiceIT {

	@LocalServerPort
	private int port;

	@Before
	public void init() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = this.port;
	}
	
	@Test
	public void testCRUDOperationsAllTogether() {
		
		final RequestSpecification reqSpec = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.build();
		
		final String newCustomerUrl = create(reqSpec);
		final Customer createdCustomer = read(reqSpec, newCustomerUrl);
		update(reqSpec, newCustomerUrl, createdCustomer);
		delete(reqSpec, newCustomerUrl);
	}

	
	/**
	 * The C of CRUD
	 * 
	 * @param reqSpec
	 * @return The URL of the created customer
	 */
	private String create(RequestSpecification reqSpec) {
		
		
		
		final Customer newCustomer = Customer.builder().customerType(CustomerType.PERSON)
				.birthDate(LocalDate.of(1990, Month.AUGUST, 16))
				.build();
		
		// Create a new customer
		final Response resp = 
			given()
				.spec(reqSpec)
				.body(newCustomer)
			.when()
				.post("/customers")
			.then()
				.assertThat()
				.statusCode(201)
				.extract().response();
		
		final String newCustomerUrl = resp.header("Location");
		assertThat(newCustomerUrl).contains("/customers/");
		
		return newCustomerUrl;
	}
	
	/**
	 * The R of CRUD
	 * 
	 * @param reqSpec
	 * @param newCustomerUrl
	 * @return The created customer
	 */
	private Customer read(RequestSpecification reqSpec, String newCustomerUrl) {
		
		// Retrieve the newly created customer
		final Customer createdCustomer =
				given()
					.spec(reqSpec)
				.when()
					.get(newCustomerUrl)
				.then()
					.assertThat()
					.statusCode(200)
					.body("id", is(notNullValue()))
					.extract()
					.as(Customer.class);
		return createdCustomer;
	}
	
	/**
	 * The U of CRUD
	 * 
	 * @param reqSpec
	 * @param newCustomerUrl
	 * @param createdCustomer
	 */
	private void update(RequestSpecification reqSpec, String newCustomerUrl, Customer createdCustomer) {
		
		final Customer customerToUpdate = Customer.builder().customerType(createdCustomer.getCustomerType())
				.birthDate(createdCustomer.getBirthDate())
				.id(createdCustomer.getId())
				.firstName("John").lastName("Doe")
				.build();
		
		// Update the customer
		given()
			.spec(reqSpec)
			.body(customerToUpdate)
		.when()
			.put(newCustomerUrl)
		.then()
			.statusCode(204);

		// Retrieve the updated customer
		final Customer updatedCustomer =
			given()
				.spec(reqSpec)
			.when()
				.get(newCustomerUrl)
			.then()
				.assertThat()
				.statusCode(200)
				.extract()
				.as(Customer.class);
		assertThat(updatedCustomer.getFirstName()).isEqualTo("John");
		assertThat(updatedCustomer.getLastName()).isEqualTo("Doe");
	}
	
	/**
	 * The D of CRUD
	 * 
	 * @param reqSpec
	 * @param newCustomerUrl
	 */
	private void delete(RequestSpecification reqSpec, String newCustomerUrl) {
		
		// Delete the customer
		given()
			.spec(reqSpec)
		.when()
			.delete(newCustomerUrl)
		.then()
			.statusCode(204);

		// Verify it has been deleted
		given()
			.spec(reqSpec)
		.when()
			.get(newCustomerUrl)
		.then()
			.assertThat()
			.statusCode(404);
	}
}