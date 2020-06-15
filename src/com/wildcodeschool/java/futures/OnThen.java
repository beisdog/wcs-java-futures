package com.wildcodeschool.java.futures;

public interface OnThen<T,R> {
	
	R then(T result);

}
