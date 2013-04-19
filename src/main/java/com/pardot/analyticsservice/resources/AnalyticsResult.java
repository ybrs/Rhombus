package com.pardot.analyticsservice.resources;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/19/13
 */
public class AnalyticsResult {

	private boolean success;
	private String message;
	private String id;
	private Object result;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
