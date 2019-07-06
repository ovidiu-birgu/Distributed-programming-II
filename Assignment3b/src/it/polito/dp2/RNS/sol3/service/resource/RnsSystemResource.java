package it.polito.dp2.RNS.sol3.service.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.RNS.sol3.service.ServiceImpl;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webrns;

/**
 * Resource class hosted at the relative root path
 */
@Path("")
@Api(value = "", description = "root element of rns system")
public class RnsSystemResource {
	private final String MESSAGE_200 = "OK";
	private final String MESSAGE_500 = "Internal Server Error";
	private ServiceImpl service;
	
	public RnsSystemResource() {
		service = ServiceImpl.getInstance();		
	}
	
	/** Method processing HTTP GET requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param uriInfo
	 * @return the main rns resource
	 */
    @GET 
    @ApiOperation(value = "get the rns system object ", notes = "xml and json formats")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message = MESSAGE_200),
    		@ApiResponse(code = 500, message = MESSAGE_500)})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Webrns getRnsSystem(@Context UriInfo uriInfo) {
    	
		ResponseService responseService = service.getRnsSystem(uriInfo.getBaseUri().toString());
    	
		elaborateStatusCode(responseService.getResponseStatus());

		return (Webrns)responseService.getBody();	
    }
    
	/**
	 * elaborate the http response
	 * and throw the corresponding exception
	 * @param response
	 */
	private void elaborateStatusCode(ResponseStatus responseHTTP) {
		InputStream templateStream = null;
		BufferedReader reader = null;
		StringBuilder out = new StringBuilder();
		String line = null;
		String responseBodyTemplate = null;
		String responseBody = null;
		Response response = null;
				
		// build response body template
		switch (responseHTTP.getStatusCode()) {
		case 400:
			templateStream = RnsSystemResource.class.getResourceAsStream("/html/BadRequestBodyTemplate.html");
			break;
		case 403:
			templateStream = RnsSystemResource.class.getResourceAsStream("/html/ForbiddenBodyTemplate.html");
			break;
		case 404:
			templateStream = RnsSystemResource.class.getResourceAsStream("/html/NotFoundBodyTemplate.html");
			break;
		case 500:
			throw new InternalServerErrorException(responseHTTP.getMessage());
		default:
			return;
		}	

		if (templateStream == null) {
			throw new InternalServerErrorException("html template file Not found.");
		}
		reader = new BufferedReader(new InputStreamReader(templateStream));
		try {
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
		} catch (IOException e) {
			throw new InternalServerErrorException("html template file Not found.", e);
		}
		
		responseBodyTemplate = out.toString();
		responseBody = responseBodyTemplate.replaceFirst("___TO_BE_REPLACED___", responseHTTP.getMessage());
		
		// add response body to exception and throw it
		switch (responseHTTP.getStatusCode()) {
		case 400:
			BadRequestException bre = new BadRequestException(responseHTTP.getMessage());
			response = Response.fromResponse(bre.getResponse()).entity(responseBody).build();
			throw new BadRequestException(responseHTTP.getMessage(), response);
		case 403:
			ForbiddenException fe = new ForbiddenException(responseHTTP.getMessage());
			response = Response.fromResponse(fe.getResponse()).entity(responseBody).build();
			throw new ForbiddenException(responseHTTP.getMessage(), response);
		case 404:
			NotFoundException nfe = new NotFoundException(responseHTTP.getMessage());
			response = Response.fromResponse(nfe.getResponse()).entity(responseBody).build();			
			throw new NotFoundException(responseHTTP.getMessage(), response);
		case 500:
			throw new InternalServerErrorException(responseHTTP.getMessage());
		default:
			return;
		}	
	}	

}
