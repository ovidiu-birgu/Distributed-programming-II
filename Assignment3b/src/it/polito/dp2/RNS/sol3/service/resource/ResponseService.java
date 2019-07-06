package it.polito.dp2.RNS.sol3.service.resource;

public class ResponseService {
	private ResponseStatus responseStatus;
	private Object body;
	
	public ResponseService(ResponseStatus responseStatus, Object body) {
		super();
		this.responseStatus = responseStatus;
		this.body = body;
	}
	
	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}
	
	public Object getBody() {
		return body;
	}
}
