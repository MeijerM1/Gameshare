package controllers;

import models.com.gamecode_share.models.User;
import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.security.Secured;
import play.data.DynamicForm;
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

    @Inject
    public UserController(FormFactory formFactory, UserRepository userRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
        this.ec = ec;
    }

    public Result index() {
        return ok(views.html.site.index.render("Home", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public CompletionStage<Result> addUser() {
        DynamicForm requestData = formFactory.form().bindFromRequest();
        char[] password = requestData.get("password").toCharArray();
        User user = new User(requestData.get("name"));
        return userRepository.add(user, password).thenApplyAsync(p -> redirect(routes.UserController.index()), ec.current());
    }
}
