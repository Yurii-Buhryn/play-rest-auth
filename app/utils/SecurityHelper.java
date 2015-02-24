package utils;

import models.User;
import play.libs.Json;
import play.mvc.Http.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SecurityHelper {
	
	// headers names
	public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
	public final static String AUTH_REST_ENABLE_HEADER = "X-AUTH-REST-ENABLE";
	
	// cookies names
	public static final String AUTH_TOKEN_COOKIE = "authToken";
	
	// context names
	public static final String AUTHORIZED_USER = "authorizedUser";
	public static final String USER_ACTIVATION = "userActivation";
	
	// response names
	public static final String AUTH_ERROR = "authError";
	public static final String AUTH_RESPONSE = "authResponse";
	
	public static enum AuthenticationStatus {
		USER_NOT_AUTHORIZED,
		USER_AUTHORIZED,
		USER_NOT_ACTIVATED,
		USER_ACTIVATED,
		USER_CREATED,
		USER_LOGOUT,
		USER_ACTIVATION_ERROR,
		INVALID_EMAIL_OR_PASSWORD,
		INVALID_ACTIVATION_TOKEN
	}
	
	public static class AuthResponseGenerator {
		public static ObjectNode generateError(AuthenticationStatus errorType){
			ObjectNode error = Json.newObject();
			error.put(AUTH_ERROR, errorType.toString());
			return error;
		}
		
		public static ObjectNode generateError(JsonNode jsonError){
			ObjectNode error = Json.newObject();
			error.put(AUTH_ERROR, jsonError);
			return error;
		}
		
		public static ObjectNode generateResponse(String responseName, String responseValue){
			ObjectNode response = Json.newObject();
			response.put(responseName, responseValue);
			return response;
		}
		
		public static ObjectNode generateResponse(AuthenticationStatus status){
			ObjectNode response = Json.newObject();
			response.put(AUTH_RESPONSE, status.toString());
			return response;
		}
	}
	
	public static User getUser(Context context) {
		 return (User)context.args.get(AUTHORIZED_USER);
	}
}
