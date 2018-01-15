package com.kubepay.webflux.customer;


import java.time.LocalDate;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Customer {

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	private String firstName;
	private String lastName;
	private Gender gender;
	@JsonFormat(shape = Shape.STRING)
	private LocalDate birthDate;
	private MaritalStatus maritalStatus;
	private Address address;
	private Map<PhoneType, String> phones;
	private String email;
	@NotNull
	private CustomerType customerType;

	
}
