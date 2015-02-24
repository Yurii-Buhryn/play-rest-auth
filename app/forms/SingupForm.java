package forms;

import play.data.validation.Constraints;

public class SingupForm {
	@Constraints.Required
	@Constraints.Email
	public String emailAddress;

	@Constraints.Required
	public String fullName;

	@Constraints.Required
	public String password;
}
