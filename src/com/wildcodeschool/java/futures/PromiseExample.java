package com.wildcodeschool.java.futures;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

public class PromiseExample {

	public static void main(String[] args) {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		Promise<String> p = HttpUtil.fetchWithPromise("http://localhost:8080/api/v1/items",executor);
		log(p.getStatus());
		log(p.getResult());

		p.then((result) -> {
			log(p.getStatus());
			System.out.println(new Date() + " " + result);
			Gson gson = new Gson();
			return gson.fromJson(result, List.class);
		}).then((result) -> {
			Map<String,?> first = (Map<String,?>) result.get(0);
			log(first.getClass()+":" + first);
			return first;
		}).then((result) -> {
			log(result.get("name"));
			throw new IllegalStateException("Some error");
			//return "Finished 3";
		})
		.catching((e) -> System.out.println("OnError: " +  e));;
		
		executor.shutdown();

	}
	
	public static void log(Object o) {
		System.out.println(new Date() + " " + o);
	}

}
