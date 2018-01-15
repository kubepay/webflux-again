package com.kubepay.webflux.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.kubepay.webflux.customer.Address;
public class AddressTest {

	@Test
	public void shouldBuildAnAddress() {

		// Given
		final Address address;
		
		

		// When
		address = Address.builder().country("Shadaloo")
				.streetNumber(110)
				.streetName("Bison street")
				.city("Shadaloo City")
				.zipcode("123456").build();

		// Then
		assertThat(address).isNotNull();
		assertThat(address.getCountry()).isEqualTo("Shadaloo");
		assertThat(address.getStreetNumber()).isEqualTo(110);
		assertThat(address.getStreetName()).isEqualTo("Bison street");
		assertThat(address.getCity()).isEqualTo("Shadaloo City");
		assertThat(address.getStateOrProvince()).isNull();
		assertThat(address.getZipcode()).isEqualTo("123456");
	}
	

}
