package global;

import play.GlobalSettings;
import play.mvc.Result;

import static play.mvc.Http.RequestHeader;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.notFound;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 25/10/12
 * Time: 5:00 AM
 * To change this template use File | Settings | File Templates.
 */
//import static play.mvc.Results.badRequest;
//import static play.mvc.Results.notFound;


public class Global extends GlobalSettings {

    public static Global globalObject = new Global();

    @Override
    public Result onError(RequestHeader request, Throwable t) {
        play.Logger.error("Global.onError(): " + request.remoteAddress());
        play.Logger.error("Global.onError(): " + t.getMessage());
        return internalServerError(views.html.error.internalServerError.render());

    }

    @Override
    public Result onHandlerNotFound(RequestHeader request) {
        play.Logger.error("Global.onHandlerNotFound(): " + request.remoteAddress());
        play.Logger.error("Global.onHandlerNotFound(): " + request.path());
        return notFound(views.html.error.notFound.render());

    }

    @Override
    public Result onBadRequest(RequestHeader request, String error) {
        play.Logger.error("Global.onBadRequest(): " + request.remoteAddress());
        play.Logger.error("Global.onBadRequest(): " + error);
        return badRequest(views.html.error.badRequest.render());

    }

}
