package com.example.caloriecounter.photo.domain;

import java.time.LocalDateTime;

public record Photo(
	long id,
	String name,
	LocalDateTime uploadDate,
	String path,
	long feedId
) {

}
