package com.wildcodeschool.java.futures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class HttpUtil {

	private static String GET(String url) throws IOException {
		HttpURLConnection con = null;
		try {
			var myurl = new URL(url);
			con = (HttpURLConnection) myurl.openConnection();
			StringBuilder content;
			try (var br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String line;
				content = new StringBuilder();
				while ((line = br.readLine()) != null) {
					content.append(line);
					content.append(System.lineSeparator());
				}
			}

			return content.toString();

		} finally {
			con.disconnect();
		}
	}

	public static Promise<String> fetchWithPromise(String url, ExecutorService executor) {
		
		Promise<String> promise = new Promise<>(() -> GET(url));
		executor.submit(promise);
		//shutdown after execution
		return promise;
	}
	
	public static CompletableFuture<String> fetchWithCompletableFuture(String url, ExecutorService executor) {
		
		Supplier<String> supplier = () -> {
			try {
				return GET(url);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
		
		CompletableFuture<String> future = CompletableFuture.supplyAsync(supplier, executor);
		
		
		//shutdown after execution
		executor.shutdown();
		return future;
	}

}
