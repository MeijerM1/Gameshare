package controllers;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.User;
import models.com.gamecode_share.security.Secured;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class AuthenticationController extends Controller {

    private final FormFactory formFactory;
    private final UserRepository userRepository;
    private final HttpExecutionContext ec;
    private Form<User> form;
    private DynamicForm dynForm;
    private Secured secured;

    @Inject
    public AuthenticationController(FormFactory formFactory, UserRepository userRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
        this.ec = ec;
        this.form = formFactory.form(User.class);
        secured = new Secured(userRepository);
        this.dynForm = formFactory.form();
    }

    public Result login() {
        return ok(views.html.authentication.login.render("Login", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), dynForm));
    }

    public Result authenticate() {
        DynamicForm tempForm = formFactory.form().bindFromRequest();
        String email = tempForm.get("email");
        char[] password = tempForm.get("password").toCharArray();

        if(tempForm.hasErrors()) {
            return login();
        }

        if(userRepository.login(email, password)) {
            System.out.println("Loggin succesful");
            session().clear();
            session("email", email);
        }

        return redirect(routes.SiteController.index());
    }

    public Result createAccount() {
        Form<User> filledForm = form.bindFromRequest();
        User user = new User();
        user.setEmail(filledForm.field("email").value());
        user.setUsername(filledForm.field("username").value());
        user.setReputation(0);
        user.setVerified(false);

        char[] password = filledForm.field("password").value().toCharArray();

        userRepository.add(user, password);

        return redirect(routes.SiteController.index());
    }

    public Result register() {
        return ok(views.html.authentication.register.render("Register", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), form));
    }


}
