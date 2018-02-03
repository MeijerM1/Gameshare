package controllers;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.security.Secured;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class SiteController extends Controller {

    private Secured secured;
    private final play.i18n.MessagesApi messagesApi;

    @Inject
    public SiteController(UserRepository userRepo, MessagesApi messagesApi) {
        secured = new Secured(userRepo);
        this.messagesApi = messagesApi;
    }

    public Result index() {
        return ok(views.html.site.index.render("Home", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx()), Http.Context.current().messages()));
    }
}
