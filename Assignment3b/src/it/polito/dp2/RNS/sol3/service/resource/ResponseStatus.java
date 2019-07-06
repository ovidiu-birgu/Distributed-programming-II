package it.polito.dp2.RNS.sol3.service.resource;

public class ResponseStatus {
	private int statusCode;
	private String message;
	
	public ResponseStatus(int statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}
	
}
