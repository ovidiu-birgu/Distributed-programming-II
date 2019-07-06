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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.RNS.sol3.service.ServiceImpl;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Places;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Vehicles;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webplace;

/**
 * Resource class hosted at the relative path "places"
 */
@Path("places")
@Api(value = "places", description = "a collection of place objects")
public class PlaceResource {
	private final String PATH_ID = "id";
	private final String PATH_PARAM_ID = "{id}";
	private final String PATH_PAGE = "page";
	private final String PATH_TYPE = "type";
	private final String PATH_VEHICLES_IN_PLACE = "{id}/vehicles";
	private final String MESSAGE_200 = "OK";
	private final String MESSAGE_404 = "Not Found";
	private final String MESSAGE_500 = "Internal Server Error";
	private ServiceImpl service;
	
	public PlaceResource() {
		service = ServiceImpl.getInstance();
	}

	/** Method processing HTTP GET requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param page
	 * @param type
	 * @param uriInfo
	 * @return all the places in the rns system
	 */
	@GET 
	@ApiOperation(value = "get the place objects ", notes = "xml and json formats")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = MESSAGE_200),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Places getPlaces(@QueryParam(PATH_PAGE) Long page, @QueryParam(PATH_TYPE) String type,
			@Context UriInfo uriInfo) {

		ResponseService responseService = service.getFilteredPlaces(uriInfo.getBaseUri().toString(), page, type);
		
		elaborateStatusCode(responseService.getResponseStatus());

		return (Places)responseService.getBody();
	}

	/** Method processing HTTP GET requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param id
	 * @param uriInfo
	 * @return the place identified by id or null otherwise
	 */
	@GET 
	@Path(PATH_PARAM_ID)
	@ApiOperation(	value = "get a single place object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = MESSAGE_200),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	public Webplace getPlace(@PathParam(PATH_ID) String id, @Context UriInfo uriInfo) {

		ResponseService responseService = service.getWebPlace(id, uriInfo.getBaseUri().toString());
		
		elaborateStatusCode(responseService.getResponseStatus());

		return (Webplace)responseService.getBody();
	}

	/** Method processing HTTP GET requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param id
	 * @param page
	 * @param uriInfo
	 * @return the vehicles that are in the place identified by id
	 */
	@GET 
	@Path(PATH_VEHICLES_IN_PLACE)
	@ApiOperation(	value = "get vehicles in place {id} object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = MESSAGE_200),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	public Vehicles getVehiclesInPlace(@PathParam(PATH_ID) String id, @QueryParam(PATH_PAGE) Long page, 
			@Context UriInfo uriInfo) {

		ResponseService responseService = service.getVehiclesInPlace(uriInfo.getBaseUri().toString(), page, id);
		
		elaborateStatusCode(responseService.getResponseStatus());

		return (Vehicles)responseService.getBody();
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
			templateStream = PlaceResource.class.getResourceAsStream("/html/BadRequestBodyTemplate.html");
			break;
		case 403:
			templateStream = PlaceResource.class.getResourceAsStream("/html/ForbiddenBodyTemplate.html");
			break;
		case 404:
			templateStream = PlaceResource.class.getResourceAsStream("/html/NotFoundBodyTemplate.html");
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
			throw new InternalServerErrorException("Unknown exception");
		}	
	}
	
}
