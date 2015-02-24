package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.mvc.Http.Context;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import play.mvc.Security;
import utils.SecurityHelper;
import utils.SecurityHelper.AuthenticationStatus;


public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(Context ctx) {
		String authToken = getAuthToken(ctx);
				
		if (authToken != null && !authToken.isEmpty()) {
			User user = models.User.findByAuthToken(authToken);
			
			if (user != null) {
				if(user.active){
					ctx.args.put(SecurityHelper.AUTHORIZED_USER, user);
					return user.getEmailAddress();
				} else {
					ctx.args.put(SecurityHelper.USER_ACTIVATION, false);
					return null;
				}
			}
		}

		return null;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
    	ObjectNode errorJson = null;
		Boolean activate = (Boolean) ctx.args.get(SecurityHelper.USER_ACTIVATION);
		
		if(activate != null && !activate){
			errorJson = SecurityHelper.AuthResponseGenerator.generateError(AuthenticationStatus.USER_NOT_ACTIVATED);
		} else {
			errorJson = SecurityHelper.AuthResponseGenerator.generateError(AuthenticationStatus.USER_NOT_AUTHORIZED);
		}

		return notFound(errorJson);
	}
	
	private String getAuthToken(Context context){
		Cookie authTokenCookie = context.request().cookie(SecurityHelper.AUTH_TOKEN_COOKIE);
		String[] authTokenHeaderValues = context.request().headers().get(SecurityHelper.AUTH_TOKEN_HEADER);
		
		if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
			return authTokenHeaderValues[0];
		} else if(authTokenCookie != null){
			return authTokenCookie.value();
		} else {
			return null;
		}
	}
}