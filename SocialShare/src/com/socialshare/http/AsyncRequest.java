package com.socialshare.http;

import java.nio.channels.ClosedByInterruptException;

public abstract class AsyncRequest<T> {

	private Thread thread = null;

	public final void stop() {
		if (null != thread) {
			thread.interrupt();
			thread = null;
		}
	}

	public abstract T doRequest() throws Exception;

	public abstract void onResult(T result);

	public abstract void onException(Exception e);
	
	public final void execute() {
		stop();
		thread = new Thread() {
			@Override
			public void run() {
				try {
					T res = doRequest();
					if (!isInterrupted()) {
						onResult(res);
					}
				} catch (ClosedByInterruptException ce) {
				} catch (InterruptedException ie) {
				} catch (Exception e) {
					if (!isInterrupted()) {
						onException(e);
					}
				}
			}
		};
		thread.start();
	}

}
