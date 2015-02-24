package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.PlayInternal;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import play.mvc.Security;


public class Secured extends Security.Authenticator {

	public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
	public static final String AUTH_TOKEN = "authToken";

	
	@Override
	public String getUsername(Context ctx) {
		User user = null;
		String authToken = null;
		
		Cookie authTokenCookie = ctx.request().cookie("authToken");
		String[] authTokenHeaderValues = ctx.request().headers().get(AUTH_TOKEN_HEADER);
		
		if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
			PlayInternal.logger().info("---------------> get user from header ");
			authToken = authTokenHeaderValues[0];
		} else if(authTokenCookie != null){
			PlayInternal.logger().info("---------------> get user from cookie ");
			authToken = authTokenCookie.value();
		}
			
		PlayInternal.logger().info("---------------> authToken : " + authToken);
		
		if (authToken != null && !authToken.isEmpty()) {
			
			user = models.User.findByAuthToken(authToken);
			
			if (user != null) {
				if(user.active){
					ctx.args.put("user", user);
					return user.getEmailAddress();
				} else {
					ctx.args.put("activate", false);
					return null;
				}
			}
		}

		return null;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
    	ObjectNode errorJson = Json.newObject();
		Boolean activate = (Boolean) ctx.args.get("activate");
		
		if(activate != null && !activate){
			PlayInternal.logger().info("---------------> user not activated ");
			errorJson.put("error", "user not activated");
		} else {
			errorJson.put("error", "user not authorized");
			PlayInternal.logger().info("---------------> user not authorized ");
		}
		
		return notFound(errorJson);
	}
}