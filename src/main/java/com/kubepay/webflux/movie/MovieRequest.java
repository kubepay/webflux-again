package com.kubepay.webflux.movie;

import lombok.Data;

@Data
public class MovieRequest {

	String id;

	String description;
	
	String rating;
	
	String title;

}
