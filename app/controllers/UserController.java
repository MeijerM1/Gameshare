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
    private Secured secured;

    @Inject
    public UserController(FormFactory formFactory, UserRepository userRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
        this.ec = ec;
        secured = new Secured(userRepository);
    }
}
