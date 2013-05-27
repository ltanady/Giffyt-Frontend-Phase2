package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 4/9/12
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("facebookAccessToken");

    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.index());

    }

}
