package it.polito.dp2.RNS.sol3.service.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehState;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehiclePath;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehiclePosition;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehicleTrack;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Vehicles;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webvehicle;

/**
 * Resource class hosted at the relative path "vehicles"
 */
@Path("vehicles")
@Api(value = "vehicles", description = "a collection of vehicle objects")
public class VehicleResource {
	private final String PATH_ID = "id";
	private final String PATH_PARAM_ID = "{id}";
	private final String PATH_PAGE = "page";
	private final String PATH_VEHICLE_MOVE = "{id}/move";
	private final String PATH_VEHICLE_STATE = "{id}/state";
	private final String PATH_VEHICLE_EXIT = "{id}/exit";
	private final String MESSAGE_200 = "OK";
	private final String MESSAGE_204 = "No Content";
	private final String MESSAGE_400 = "Bad Request";
	private final String MESSAGE_403 = "Forbidden";
	private final String MESSAGE_404 = "Not Found";
	private final String MESSAGE_500 = "Internal Server Error";
	private ServiceImpl service;

	public VehicleResource() {
		service = ServiceImpl.getInstance();
	}

	/** Method processing HTTP GET requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param page
	 * @param uriInfo
	 * @return all the vehicles in the rns system
	 */
	@GET 
	@ApiOperation(value = "get the vehicle objects ", notes = "xml and json formats")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = MESSAGE_200),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Vehicles getVehicles(@QueryParam(PATH_PAGE) Long page, @Context UriInfo uriInfo) {

		ResponseService responseService = service.getVehicles(uriInfo.getBaseUri().toString(), page);
    	
		elaborateStatusCode(responseService.getResponseStatus());

		return (Vehicles)responseService.getBody();
	}

	/** Method processing HTTP POST requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param vehicleTrack
	 * @param uriInfo
	 * @return the path of the vehicle
	 */
	@POST
	@ApiOperation(	value = "create a new vehicle object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = MESSAGE_200),
			@ApiResponse(code = 400, message = MESSAGE_400),
			@ApiResponse(code = 403, message = MESSAGE_403),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public VehiclePath postVehicle(VehicleTrack vehicleTrack, @Context UriInfo uriInfo) {

		ResponseService responseService = service.trackVehicle(vehicleTrack, uriInfo.getBaseUri().toString());
		
		elaborateStatusCode(responseService.getResponseStatus());

		return (VehiclePath)responseService.getBody();	
	}	
	
	/** Method processing HTTP GET requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param id
	 * @return the vehicle identified by {id}
	 */
	@GET 
	@Path(PATH_PARAM_ID)
	@ApiOperation(	value = "get a single vehicle object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = MESSAGE_200),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	public Webvehicle getVehicle(@PathParam(PATH_ID) String id) {

		ResponseService responseService = service.getWebVehicle(id);
    	
		elaborateStatusCode(responseService.getResponseStatus());

		return (Webvehicle)responseService.getBody();
	}

	/** Method processing HTTP DELETE requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param id
	 * @return status code 204 if operation was successful
	 */
	@DELETE
	@Path(PATH_PARAM_ID)
	@ApiOperation(	value = "remove a vehicle object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = MESSAGE_204),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	public Response deleteVehicle(@PathParam(PATH_ID) String id) {

		ResponseService responseService = service.deleteVehicle(id);
    	
		elaborateStatusCode(responseService.getResponseStatus());

		return Response.noContent().build();
	}

	/** Method processing HTTP PUT requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param id
	 * @param vehiclePosition
	 * @return status code 204 if operation was successful
	 */
	@PUT
	@Path(PATH_VEHICLE_EXIT)
	@ApiOperation(	value = "remove a vehicle object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = MESSAGE_204),
			@ApiResponse(code = 400, message = MESSAGE_400),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public Response putExitVehicle(@PathParam(PATH_ID) String id, VehiclePosition vehiclePosition) {
		
		ResponseService responseService = service.untrackVehicle(id, vehiclePosition.getPlace());
    	
		elaborateStatusCode(responseService.getResponseStatus());

		return Response.noContent().build();
	}
	
	/** Method processing HTTP PUT requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param id
	 * @param vehiclePosition
	 * @return the new path for the vehicle identified by {id}
	 */
	@PUT
	@Path(PATH_VEHICLE_MOVE)
	@ApiOperation(	value = "update position of vehicle object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = MESSAGE_200),
			@ApiResponse(code = 400, message = MESSAGE_400),
			@ApiResponse(code = 403, message = MESSAGE_403),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public VehiclePath putVehiclePosition(@PathParam(PATH_ID) 
		String id, VehiclePosition vehiclePosition) {		
		
		ResponseService responseService = service.moveVehicle(id, vehiclePosition);

		elaborateStatusCode(responseService.getResponseStatus());

		return (VehiclePath) responseService.getBody();
	} 	
	
	/** Method processing HTTP PUT requests, producing
	 * "application/xml" or "application/json" MIME media type.
	 * @param id
	 * @param vehState
	 * @return status code 204 if operation was successful
	 */
	@PUT
	@Path(PATH_VEHICLE_STATE)
	@ApiOperation(	value = "update state of vehicle object", notes = "xml and json formats")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = MESSAGE_204),
			@ApiResponse(code = 400, message = MESSAGE_400),
			@ApiResponse(code = 404, message = MESSAGE_404),
			@ApiResponse(code = 500, message = MESSAGE_500)})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
	@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public Response putVehicleState(@PathParam(PATH_ID) String id, VehState vehState) {

		ResponseService responseService = service.changeStateVehicle(id, vehState.getState());

		elaborateStatusCode(responseService.getResponseStatus());

		return Response.noContent().build();	
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
			templateStream = VehicleResource.class.getResourceAsStream("/html/BadRequestBodyTemplate.html");
			break;
		case 403:
			templateStream = VehicleResource.class.getResourceAsStream("/html/ForbiddenBodyTemplate.html");
			break;
		case 404:
			templateStream = VehicleResource.class.getResourceAsStream("/html/NotFoundBodyTemplate.html");
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
