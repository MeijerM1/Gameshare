package models.com.gamecode_share.security;

import models.com.gamecode_share.database.Interfaces.UserRepository;
import models.com.gamecode_share.models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

public class Secured extends Security.Authenticator {

    private static UserRepository userRepo;

    @Inject
    public Secured(UserRepository userRepo) {
        Secured.userRepo = userRepo;
    }

    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get("email");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        // TODO return to login page.
        return null;
    }

    public static String getUser(Http.Context ctx) {
        return ctx.session().get("email");
    }

    public static boolean isLoggedIn(Http.Context ctx) {
        return (getUser(ctx) != null);
    }

    public static User getUserInfo(Http.Context ctx) {
        return userRepo.getUserByEmail(getUser(ctx));
    }
}