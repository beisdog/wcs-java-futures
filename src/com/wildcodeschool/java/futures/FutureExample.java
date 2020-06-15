package com.wildcodeschool.java.futures;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

public class FutureExample {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		CompletableFuture<String> p = HttpUtil.fetchWithCompletableFuture("http://localhost:8080/api/v1/items",executor);
		
		log(p.getNow(null));
		
		p.thenApply((result) -> {
			System.out.println(new Date() + " " + result);
			Gson gson = new Gson();
			return gson.fromJson(result, List.class);
		}).thenApply((result) -> {
			Map<String,?> first = (Map<String,?>) result.get(0);
			log(first.getClass()+":" + first);
			return first;
		}).thenApply((result) -> {
			log(result.get("name"));
			throw new IllegalStateException("Some error");
			//return "Finished 3";
		})
		.exceptionally((e) -> {
			System.out.println("OnError: " +  e);
			return null;
		})
		;
		
		executor.shutdown();

	}
	
	public static void log(Object o) {
		System.out.println(new Date() + " " + o);
	}

}
