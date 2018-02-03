package models.com.gamecode_share.security;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

public class Secured extends Security.Authenticator {

    private final UserRepository userRepo;

    @Inject
    public Secured(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String getEmail(Http.Context ctx) {
        return ctx.session().get("email");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(controllers.routes.SiteController.index());
    }

    private static String getUserEmail(Http.Context ctx) {
        return ctx.session().get("email");
    }

    public static boolean isLoggedIn(Http.Context ctx) {
        return (getUserEmail(ctx) != null);
    }

    public User getUserInfo(Http.Context ctx) {
        return userRepo.getUserByEmail(getUserEmail(ctx));
    }
}