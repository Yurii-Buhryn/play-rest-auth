package forms;

import play.data.validation.Constraints;

public class LoginForm {
	@Constraints.Required
	@Constraints.Email
	public String emailAddress;

	@Constraints.Required
	public String password;
}
