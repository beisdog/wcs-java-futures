package com.wildcodeschool.java.futures;

@FunctionalInterface
public interface SupplierWithException<T> {
	
	T get() throws Exception;

}
