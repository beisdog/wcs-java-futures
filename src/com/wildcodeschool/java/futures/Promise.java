package com.wildcodeschool.java.futures;

public class Promise<T> implements Runnable {

	public static enum Status {
		PENDING, RUNNING, FINISHED, FAILED;
	}

	private SupplierWithException<T> supplier;
	private T result;
	private Status status = Status.PENDING;
	private Exception exception;

	private OnSuccess<T> onSuccess;
	private OnError onError;
	private Promise<?> next;

	public Promise(SupplierWithException<T> supplier) {
		super();
		this.supplier = supplier;
	}

	public Promise(SupplierWithException<T> supplier, OnSuccess<T> onSuccess, OnError onError) {
		super();
		this.supplier = supplier;
		this.onSuccess = onSuccess;
		this.onError = onError;
	}

	public void run() {
		synchronized (this) {

			status = Status.RUNNING;
			try {
				// execute supplier
				this.result = supplier.get();
				this.status = Status.FINISHED;
				if (this.onSuccess != null) {
					this.onSuccess.onSuccess(this.result);
				}
				// Execute then 
				if (this.next != null) {
					this.next.run();
				}
			} catch (Exception e) {
				this.exception = e;
				this.status = Status.FAILED;
				if (this.onError != null) {
					this.onError.onError(e);
				}
			} finally {
				this.notifyAll();
			}
		}

	}
	
	public Promise<T> setOnSuccess(OnSuccess<T> onSuccess) {
		this.onSuccess = onSuccess;
		return this;
	}

	public Promise<T> setOnError(OnError onError) {
		this.onError = onError;
		return this;
	}

	public T getResult() {
		return result;
	}

	public Status getStatus() {
		return status;
	}

	public Exception getException() {
		return exception;
	}
	
	public static <T> T await(Promise<T> p) {
		synchronized (p) {
			return p.getResult();
		}
	}

	public <R> Promise<R> then(OnThen<T, R> onThen) {
		Promise<R> p = new Promise<>( () -> onThen.then(result));
		p.setOnError(this.onError);
		this.next = p;
		return p;
	}
	
	public void catching(OnError onError) {
		this.setOnError(onError);
	}
}
