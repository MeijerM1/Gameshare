package controllers;

import models.com.gamecode_share.models.User;
import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.security.Secured;
import models.com.gamecode_share.utility.MailService;
import play.api.libs.mailer.MailerClient;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * The controller keeps all database operations behind the repository, and uses
 * {@link play.libs.concurrent.HttpExecutionContext} to provide access to the
 * {@link play.mvc.Http.Context} methods like {@code request()} and {@code flash()}.
 */
public class UserController extends Controller {

    private final FormFactory formFactory;
    private final UserRepository userRepository;
    private final HttpExecutionContext ec;
    private Form<User> form;
    private Secured secured;

    @Inject
    public UserController(FormFactory formFactory, UserRepository userRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
        this.ec = ec;
        secured = new Secured(userRepository);
        this.form = formFactory.form(User.class);
    }

    public Result editAccount() {
        User user = form.get();
        userRepository.editUser(user);
        return redirect(routes.SiteController.index());
    }

    public Result profile() {
        return ok(views.html.account.profile.render("Profile", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), form));
    }

    @com.google.inject.Inject
    MailerClient mailerClient;
    public Result sendEmail() {
        MailService mailer = new MailService(mailerClient);

        mailer.sendSignUpConfirmation("mpw.meijer@gmail.com");

        return ok();
    }
}
