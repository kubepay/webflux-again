package com.kubepay.webflux.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Address {

	private Integer streetNumber;
	private String streetName;
	private String city;
	private String zipcode;
	private String stateOrProvince;
	private String country;

}
