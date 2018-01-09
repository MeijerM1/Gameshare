package controllers;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.security.Secured;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class SiteController extends Controller {

    private Secured secured;

    @Inject
    public SiteController(UserRepository userRepo) {
        secured = new Secured(userRepo);
    }

    public Result index() {
        return ok(views.html.site.index.render("Home", Secured.isLoggedIn(ctx()), secured.getUserInfo(ctx())));
    }
}
