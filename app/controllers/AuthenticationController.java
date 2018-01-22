package controllers;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.User;
import models.com.gamecode_share.security.Secured;
import models.com.gamecode_share.utility.MailService;
import play.api.libs.mailer.MailerClient;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
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
        return ok(views.html.authentication.login.render("Login", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), dynForm, Http.Context.current().messages()));
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
        user.setJoinDate(new Date());

        char[] password = filledForm.field("password").value().toCharArray();

        userRepository.add(user, password);

        user.generateVerificationCode();
        sendEmail(user);

        userRepository.updateUser(user);

        return ok(views.html.authentication.accountCreateConfirmation.render("Register", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), Http.Context.current().messages()));
    }

    public Result register() {
        return ok(views.html.authentication.register.render("Register", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), form, Http.Context.current().messages()));
    }

    public Result verifyAccount(String email, String verificationCode  ) {
        boolean result = userRepository.verifyUser(email , verificationCode);

        if(result) {
            return ok(views.html.authentication.accountVerifyConfirmation.render("Verify", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), Http.Context.current().messages()));
        } else {
            return ok(views.html.authentication.accountVerifyError.render("Verify", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), Http.Context.current().messages(), email));
        }
    }

    public Result sendVerificationEmail(String email) {
        User user  = userRepository.getUserByEmail(email);

        user.generateVerificationCode();
        sendEmail(user);

        return ok();
    }

    @com.google.inject.Inject
    MailerClient mailerClient;
    public void sendEmail(User user) {
        MailService mailer = new MailService(mailerClient);

        mailer.sendSignUpConfirmation(user);
    }


}
