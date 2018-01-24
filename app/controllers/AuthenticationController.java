package controllers;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.Role;
import models.com.gamecode_share.models.User;
import models.com.gamecode_share.security.Secured;
import models.com.gamecode_share.services.UserService;
import models.com.gamecode_share.utility.MailService;
import play.api.libs.mailer.MailerClient;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Date;

public class AuthenticationController extends Controller {

    private final FormFactory formFactory;
    private final UserRepository userRepository;
    private final HttpExecutionContext ec;
    private UserService userService;
    private Form<User> form;
    private DynamicForm dynForm;
    private Secured secured;

    @Inject
    public AuthenticationController(FormFactory formFactory, UserRepository userRepository, HttpExecutionContext ec, MailerClient client) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
        this.ec = ec;
        this.form = formFactory.form(User.class);
        secured = new Secured(userRepository);
        userService = new UserService(userRepository, client);
        this.dynForm = formFactory.form();
    }

    public Result login() {
        return ok(views.html.authentication.login.render("Login", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), dynForm, Http.Context.current().messages()));
    }

    public Result authenticate() {
        DynamicForm tempForm = formFactory.form().bindFromRequest();

        if(tempForm.hasErrors()) {
            return login();
        }

        Messages messages = Http.Context.current().messages();

        boolean result = userService.login(tempForm);

        if(result) {
            System.out.println("Login successful");
            session().clear();
            session("email", tempForm.get("email"));
            User user = secured.getUserInfo(ctx());

            if (user.getRole() == Role.BANNEDUSER) {
                DynamicForm returnForm = (DynamicForm) tempForm.withGlobalError(messages.at("auth.login.banError"));
                return badRequest(views.html.authentication.login.render("Login", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), returnForm ,messages));
            } else if (!user.isVerified()) {
                DynamicForm returnForm = (DynamicForm) tempForm.withGlobalError(messages.at("auth.login.verifyError"));
                return badRequest(views.html.authentication.login.render("Login", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), returnForm ,messages));
            }

            return redirect(routes.SiteController.index());
        } else {
            DynamicForm returnForm = (DynamicForm) tempForm.withGlobalError(messages.at("authentication.login.loginError"));
            return badRequest(views.html.authentication.login.render("Login", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), returnForm ,messages));
        }
    }

    public Result createAccount() {
        Form<User> filledForm = form.bindFromRequest();

        if(filledForm.hasErrors()) {
            return badRequest(views.html.authentication.register.render("Register", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), filledForm, Http.Context.current().messages()));
        }

        userService.add(filledForm);

        return ok(views.html.authentication.accountCreateConfirmation.render("Register", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), Http.Context.current().messages()));
    }

    public Result register() {
        return ok(views.html.authentication.register.render("Register", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), form, Http.Context.current().messages()));
    }

    public Result verifyAccount(String email, String verificationCode  ) {
        boolean result = userService.verifyAccount(email, verificationCode);

        if(result) {
            return ok(views.html.authentication.accountVerifyConfirmation.render("Verify", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), Http.Context.current().messages()));
        } else {
            return ok(views.html.authentication.accountVerifyError.render("Verify", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), Http.Context.current().messages(), email));
        }
    }

    public Result sendVerificationEmail(String email) {
        userService.sendVerificationEmail(email);

        return ok();
    }

    public Result checkEmail(String email) {
        boolean result = userService.checkEmail(email);

        if(result) {
            return ok("user_exists");
        } else {
            return ok("user_nonexists");
        }
    }

    public Result checkUsername(String username) {
        boolean result = userService.checkUsername(username);

        if(result) {
            return ok("user_exists");
        } else {
            return ok("user_nonexists");
        }
    }
}
