package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.PlayInternal;
import play.libs.Json;
import play.mvc.*;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.ebean.Transactional;
import views.html.*;

public class Application extends Controller {

	private static User getUser() {
		 return (User)Http.Context.current().args.get("user");
	}
	 
    public static Result index() {
        return ok(index.render("Play REST auth"));
    }
    
    public static Result activate(String activateToken){
    	User user = User.findByActivateToken(activateToken);
    	ObjectNode activationJson = Json.newObject();
    	
    	if(user != null){
    		Boolean activation = user.activate(activateToken);
    		activationJson.put("status", activation ? "user activated" : "error during activation");
    		return activation ? ok(activationJson) : badRequest(activationJson);
    	} else {
    		activationJson.put("status", "incorrect activation token");
    		return badRequest(activationJson);
    	}
    }
    
    @Security.Authenticated(Secured.class)
    public static Result secure() {
    	User user = getUser();
    	ObjectNode uaserJson = Json.newObject();
    	uaserJson.put("username", user.fullName);
        return ok(uaserJson);
    }
    
    @Security.Authenticated(Secured.class)
    public static Result logout() {
	    response().discardCookie(Secured.AUTH_TOKEN);
	    ObjectNode uaserJson = Json.newObject();
	    uaserJson.put("message", "logout");
	    return ok(uaserJson);
    }
    
    @Transactional
    public static Result singup() {
    	PlayInternal.logger().info("---------------> singup ");
    	
    	Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
    	
    	if (loginForm.hasErrors()) {
    		return badRequest(loginForm.errorsAsJson());
    	}
    	
    	Login login = loginForm.get();
    	User newUser = new User(login.emailAddress, login.password, "new user");
    	newUser.save();
    	newUser.generateActivationToken();
    	
    	PlayInternal.logger().info("---------------> activation token : " + newUser.activationToken);
    	
		return status(OK);
    }
    
    public static Result login() {
    	PlayInternal.logger().info("---------------> login ");
    	
    	Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
    	
    	if (loginForm.hasErrors()) {
    		return badRequest(loginForm.errorsAsJson());
    	}
    	
    	Login login = loginForm.get();
    	User user = User.findByEmailAddressAndPassword(login.emailAddress, login.password);
    	
    	if (user == null || !user.active) {
    		PlayInternal.logger().info("---------------> notFound ");
    		return notFound();
    		
    	} else {
    		
	    	String authToken = user.createToken();
	    	ObjectNode authTokenJson = Json.newObject();
	    	authTokenJson.put(Secured.AUTH_TOKEN, authToken);
	    	response().setCookie(Secured.AUTH_TOKEN, authToken);
	    	PlayInternal.logger().info("---------------> sucsess ");
	    	
	    	return ok(authTokenJson);
    	}
    }
    
    public static class Login {
    	@Constraints.Required
    	@Constraints.Email
    	public String emailAddress;
    	
    	@Constraints.Required
    	public String password;
    }

}
