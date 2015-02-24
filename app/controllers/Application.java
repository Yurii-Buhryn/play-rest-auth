package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.SecurityHelper;
import views.html.index;

public class Application extends Controller {
	 
    public static Result index() {
        return ok(index.render("Play REST auth"));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result secure() {
    	User user = SecurityHelper.getUser(Http.Context.current());
        return ok(SecurityHelper.AuthResponseGenerator.generateResponse("user", user.fullName));
    }

}
