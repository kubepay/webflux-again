package com.kubepay.webflux.movie;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class Movie {
	
	@Id
	String id;
	
	String description;
	
	String rating;
	
	String title;

}
