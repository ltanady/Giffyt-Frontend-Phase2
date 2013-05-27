package controllers;

import global.Global;
import helper.Constants;
import models.User;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import play.*;
import play.data.DynamicForm;
import play.i18n.Messages;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.mvc.*;

import request.APIRequest;
import util.ConfigReader;
import views.html.*;

import java.util.Date;
import java.util.HashMap;

import static play.data.Form.form;

public class Application extends Controller {
  
    public static Result index() {
        play.Logger.info("Application.index(): start");
        return ok(views.html.index.render());

    }

    /**
     * Login function, calls giffyt api.
     * @return Result
     */
    public static Result login() {
        play.Logger.info("Application.login(): start");
        DynamicForm data = form().bindFromRequest();
        final String facebookAccessToken = data.get("facebookAccessToken");
        final String facebookEmail = data.get("facebookEmail");

        if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Application.login(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }

        if(facebookEmail == null || facebookEmail.isEmpty()) {
            play.Logger.error("Application.login(): " + Messages.get("MISSING_PARAMETER", "facebookEmail"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookEmail"));

        }
        play.Logger.info("Application.login(): " + facebookEmail);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String url = ConfigReader.getValue(Constants.ENVIRONMENT) + "/login";

        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("facebookAccessToken", facebookAccessToken);

        try {
            play.Logger.info("Application.login(): calling API");
            APIRequest request = new APIRequest(url, "application/json");
            JsonNode jsonRequest = Json.toJson(requestMap);
            F.Promise<WS.Response> responsePromise = request.post(jsonRequest.toString());
            F.Promise<Result> resultPromise = responsePromise.map(new F.Function<WS.Response, Result>() {
                @Override
                public Result apply(WS.Response response) throws Throwable {
                    User user = null;
                    if (response != null) {
                        JsonNode responseNode = response.asJson();
                        if (responseNode != null) {
                            JsonNode dataNode = responseNode.findPath("data");
                            JsonNode messageNode = responseNode.findPath("message");

                            if(response.getStatus() == Http.Status.BAD_REQUEST) {
                                play.Logger.error("Application.login(): BadRequest, " + messageNode.getTextValue());
                                return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                            }

                            if(response.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                                play.Logger.error("Application.login(): InternalServerError, " + messageNode.getTextValue());
                                throw new Exception(messageNode.getTextValue());

                            }
                            user = Json.fromJson(dataNode, User.class);
                            if(user == null) {
                                play.Logger.error("Application.login(): BadRequest, " + Messages.get("NULL_OBJECT", "User"));
                                return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "User"));

                            }

                        }

                    }
                    session("facebookAccessToken", facebookAccessToken);
                    session("facebookId", user.facebookId);
                    session("name", user.name);
                    return ok(user.name);
                }
            });
            play.Logger.info("Application.login(): API call OK");

            return async(resultPromise);

        }catch(Exception e) {
            play.Logger.error("Application.login(): Exception");
            play.Logger.error("Application.login(): " + facebookAccessToken);
            return Global.globalObject.onError(request(), e);

        }

    }

    /**
     * Logout function, clear session at the same time.
     * @return
     */
    public static Result logout() {
        play.Logger.info("Application.logout(): start");
        session().clear();
        play.Logger.info("Application.logout(): cleared cache");

        return redirect(routes.Application.index());

    }

    public static Result tos() {
        return ok(views.html.tos.render());
    }

    public static Result privacy() {
        return ok(views.html.privacy.render());
    }

    public static Result faq() {
        return ok(views.html.faq.render());
    }

    public static Result contactUs() {
        return ok(views.html.contactUs.render());
    }

}
