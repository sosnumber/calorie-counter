package com.example.caloriecounter.exception;

public interface ExceptionFunction<T, R> {
	R apply(T r) throws Exception;
}
