package controllers;

import global.Global;
import helper.Constants;
import models.Product;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import play.i18n.Messages;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import request.APIRequest;
import util.ConfigReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 2/10/12
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
//@Security.Authenticated(Secured.class)
public class Products extends Controller {

    /**
     * Get list of items given the countryCode and budget.
     * Negative budget by default means no budget is supplied.
     * @param countryCode
     * @param budget
     * @return
     */
    public static Result list(String countryCode, Double budget) {
        play.Logger.info("Products.list(): start");

        final String facebookAccessToken = session().get("facebookAccessToken");
        if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Products.list(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }

        String url = ConfigReader.getValue(Constants.ENVIRONMENT) + "/products/list";

        HashMap<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("facebookAccessToken", facebookAccessToken);
        requestMap.put("countryCode", countryCode);
        requestMap.put("budget", (budget >= 0.0) ? budget : null);

        try {
            play.Logger.info("Products.list(): calling Products List API");
            APIRequest request = new APIRequest(url, "application/json");
            JsonNode jsonRequest = Json.toJson(requestMap);
            F.Promise<WS.Response> responsePromise = request.post(jsonRequest.toString());
            F.Promise<Result> resultPromise = responsePromise.map(new F.Function<WS.Response, Result>() {
                @Override
                public Result apply(WS.Response response) throws Throwable {
                    List<Product> items = new ArrayList<Product>();
                    LinkedHashMap<String, List<Product>> productsMap = new LinkedHashMap<String, List<Product>>();

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    Double minValue = 0.0;
                    Double maxValue = 0.0;

                    if(response != null) {
                        JsonNode responseNode = response.asJson();
                        if(responseNode != null) {
                            JsonNode productsNode = responseNode.findPath("data");
                            JsonNode messageNode = responseNode.findPath("message");

                            if(response.getStatus() == Http.Status.BAD_REQUEST) {
                                play.Logger.error("Products.list(): BadRequest, " + messageNode.getTextValue());
                                return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                            }

                            if(response.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                                play.Logger.error("Products.list(): InternalServerError, " + messageNode.getTextValue());
                                throw new Exception(messageNode.getTextValue());

                            }
                            TypeReference<LinkedHashMap<String, List<Product>>> typeRef
                                    = new TypeReference<
                                    LinkedHashMap<String, List<Product>>
                                    >() {};
                            productsMap = objectMapper.readValue(productsNode, typeRef);
                            if(productsMap == null)
                                productsMap = new LinkedHashMap<String, List<Product>>();

                        }

                    }
                    play.Logger.info("Products.list(): API call OK");

                    return ok(views.html.itemsList.render(productsMap));

                }
            });
            return async(resultPromise);

        }catch(Exception e) {
            play.Logger.error("Products.list(): Exception");
            return Global.globalObject.onError(request(), e);

        }

    }

    public static Result listBudget(Double budget) {
        System.out.println(budget);

        return ok();

    }


}
