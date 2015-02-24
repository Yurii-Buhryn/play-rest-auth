package controllers;

import models.User;
import play.PlayInternal;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.SecurityHelper;
import utils.SecurityHelper.AuthResponseGenerator;
import utils.SecurityHelper.AuthenticationStatus;
import forms.LoginForm;
import forms.SingupForm;

public class SecurityRestController extends Controller {
	
    @Transactional
    public static Result singup() {    	
    	Form<SingupForm> singupForm = Form.form(SingupForm.class).bindFromRequest();
    	
    	if (singupForm.hasErrors()) {
    		return badRequest(AuthResponseGenerator.generateError(singupForm.errorsAsJson()));
    	}
    	
    	SingupForm singup = singupForm.get();
    	User singUpUser = new User(singup.emailAddress, singup.password, singup.fullName);
    	singUpUser.save();
    	singUpUser.generateActivationToken();
    	
    	PlayInternal.logger().info("---------------> activation token : " + singUpUser.activationToken);
    	
		return ok(AuthResponseGenerator.generateResponse(AuthenticationStatus.USER_CREATED));
    }
    
    @Transactional
    public static Result activate(String activateToken){
    	User user = User.findByActivateToken(activateToken);
    	
    	if(user != null){
    		Boolean activation = user.activate(activateToken);
    		return activation ? 
    				ok(AuthResponseGenerator.generateResponse(AuthenticationStatus.USER_ACTIVATED)) : 
    				badRequest(AuthResponseGenerator.generateResponse(AuthenticationStatus.USER_ACTIVATION_ERROR));
    	} else {
    		return badRequest(AuthResponseGenerator.generateError(AuthenticationStatus.INVALID_ACTIVATION_TOKEN));
    	}
    }
    
    public static Result login() {
    	Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();
    	
    	if (loginForm.hasErrors()) {
    		return badRequest(AuthResponseGenerator.generateError(loginForm.errorsAsJson()));
    	}
    	
    	LoginForm login = loginForm.get();
    	User user = User.findByEmailAddressAndPassword(login.emailAddress, login.password);
    	
    	if (user == null){
    		return notFound(AuthResponseGenerator.generateError(AuthenticationStatus.INVALID_EMAIL_OR_PASSWORD));
    	} else if (!user.active) { 
    		return notFound(AuthResponseGenerator.generateError(AuthenticationStatus.USER_NOT_ACTIVATED));
    	}else {
    		String authToken = user.createToken();
	    	response().setCookie(SecurityHelper.AUTH_TOKEN_COOKIE, authToken);
	    	return ok(AuthResponseGenerator.generateResponse(SecurityHelper.AUTH_TOKEN_COOKIE, authToken));
    	}
    }
    
    @Security.Authenticated(Secured.class)
    public static Result logout() {
	    response().discardCookie(SecurityHelper.AUTH_TOKEN_COOKIE);
	    return ok(AuthResponseGenerator.generateResponse(AuthenticationStatus.USER_LOGOUT));
    }
}
