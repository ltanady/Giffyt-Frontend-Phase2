package controllers;

import global.Global;
import helper.Constants;
import models.Gift;
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
import request.APIRequest;
import util.ConfigReader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 18/5/13
 * Time: 3:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gifts extends Controller {

    /**
     * Get gifts count with PENDING_RECIPIENT status.
     * @return Result
     */
    public static Result count() {
        play.Logger.info("Gifts.count(): start");
        // Get Facebook access token from cookie
        final String facebookAccessToken = session().get("facebookAccessToken");

        if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Gifts.count(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }

        String url = ConfigReader.getValue(Constants.ENVIRONMENT) + "/gifts/count";

        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("facebookAccessToken", facebookAccessToken);

        try {
            play.Logger.info("Gifts.count(): calling API");
            APIRequest request = new APIRequest(url, "application/json");
            JsonNode jsonRequest = Json.toJson(requestMap);
            F.Promise<WS.Response> responsePromise = request.post(jsonRequest.toString());
            F.Promise<Result> resultPromise = responsePromise.map(new F.Function<WS.Response, Result>() {
                @Override
                public Result apply(WS.Response response) throws Throwable {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    Long giftsCount = 0L;

                    if(response != null) {
                        JsonNode responseNode = response.asJson();
                        if(responseNode != null) {
                            JsonNode giftsCountNode = responseNode.findPath("data");
                            JsonNode messageNode = responseNode.findPath("message");

                            if(response.getStatus() == Http.Status.BAD_REQUEST) {
                                play.Logger.error("Gifts.count(): BadRequest, " + messageNode.getTextValue());
                                return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                            }

                            if(response.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                                play.Logger.error("Gifts.count(): InternalServerError, " + messageNode.getTextValue());
                                throw new Exception(messageNode.getTextValue());

                            }
                            giftsCount = giftsCountNode.asLong();

                        }
                    }
                    play.Logger.info("Gifts.count(): API call OK");
                    return ok(giftsCount.toString());

                }
            });
            return async(resultPromise);

        }catch(Exception e) {
            play.Logger.error("Gifts.count(): Exception");
            play.Logger.error("Gifts.count(): " + facebookAccessToken);
            return Global.globalObject.onError(request(), e);

        }


    }

    /**
     * Get list of gifts with PENDING_RECIPIENT status.
     * @return Result
     */
    public static Result list() {
        play.Logger.info("Gifts.list(): start");
        // Get Facebook access token from cookie
        final String facebookAccessToken = session().get("facebookAccessToken");

        if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Gifts.count(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }

        String url = ConfigReader.getValue(Constants.ENVIRONMENT) + "/gifts/list";

        HashMap<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("facebookAccessToken", facebookAccessToken);

        try {
            play.Logger.info("Gifts.list(): calling API");
            APIRequest request = new APIRequest(url, "application/json");
            JsonNode jsonRequest = Json.toJson(requestMap);
            F.Promise<WS.Response> responsePromise = request.post(jsonRequest.toString());
            F.Promise<Result> resultPromise = responsePromise.map(new F.Function<WS.Response, Result>() {
                @Override
                public Result apply(WS.Response response) throws Throwable {
                    List<Gift> gifts = null;

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    if(response != null) {
                        JsonNode responseNode = response.asJson();
                        if(responseNode != null) {
                            JsonNode giftsNode = responseNode.findPath("data");
                            JsonNode messageNode = responseNode.findPath("message");

                            if(response.getStatus() == Http.Status.BAD_REQUEST) {
                                play.Logger.error("Gifts.list(): BadRequest, " + messageNode.getTextValue());
                                return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                            }

                            if(response.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                                play.Logger.error("Gifts.list(): InternalServerError, " + messageNode.getTextValue());
                                throw new Exception(messageNode.getTextValue());

                            }

                            TypeReference<List<Gift>> typeRef = new TypeReference<List<Gift>>() {};
                            gifts = objectMapper.readValue(giftsNode, typeRef);

                        }

                    }
                    play.Logger.info("Gifts.list(): API call OK");
                    return ok(views.html.gift_list.render(gifts));

                }
            });
            return async(resultPromise);

        }catch(Exception e) {
            play.Logger.error("Gifts.list(): Exception");
            play.Logger.error("Gifts.list(): " + facebookAccessToken);
            return Global.globalObject.onError(request(), e);

        }


    }

    /**
     * Get gift with temporaryToken
     * @param temporaryToken
     * @return Result
     */
    public static Result get(String temporaryToken) {
        play.Logger.info("Gifts.get(): start");

        if(temporaryToken == null || temporaryToken.isEmpty()) {
            play.Logger.error("Gifts.count(): " + Messages.get("MISSING_PARAMETER", "temporaryToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "temporaryToken"));

        }
        String url = ConfigReader.getValue(Constants.ENVIRONMENT) + "/gifts/get";

        HashMap<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("temporaryToken", temporaryToken);

        try {
            play.Logger.info("Gifts.get(): calling API");
            APIRequest request = new APIRequest(url, "application/json");
            JsonNode jsonRequest = Json.toJson(requestMap);
            F.Promise<WS.Response> responsePromise = request.post(jsonRequest.toString());
            F.Promise<Result> resultPromise = responsePromise.map(new F.Function<WS.Response, Result>() {
                @Override
                public Result apply(WS.Response response) throws Throwable {
                    Gift gift = null;

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    if(response != null) {
                        JsonNode responseNode = response.asJson();
                        if(responseNode != null) {
                            JsonNode dataNode = responseNode.findPath("data");
                            JsonNode messageNode = responseNode.findPath("message");
                            if(dataNode != null) {
                                JsonNode giftsNode = dataNode.findPath("gift");

                                if(response.getStatus() == Http.Status.BAD_REQUEST) {
                                    play.Logger.error("Gifts.get(): BadRequest, " + messageNode.getTextValue());
                                    return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                                }

                                if(response.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                                    play.Logger.error("Gifts.get(): InternalServerError, " + messageNode.getTextValue());
                                    throw new Exception(messageNode.getTextValue());

                                }
                                gift = objectMapper.readValue(giftsNode, Gift.class);

                            }

                        }

                    }
                    play.Logger.info("Gifts.get(): API call OK");
                    return ok(views.html.receive.render(gift));

                }
            });
            return async(resultPromise);

        }catch(Exception e) {
            play.Logger.error("Gifts.get(): Exception");
            play.Logger.error("Gifts.get(): " + temporaryToken);
            return Global.globalObject.onError(request(), e);

        }

    }

    public static Result open(String temporaryToken) {
        play.Logger.info("Gifts.open(): start");

        if(temporaryToken == null || temporaryToken.isEmpty()) {
            play.Logger.error("Gifts.count(): " + Messages.get("MISSING_PARAMETER", "temporaryToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "temporaryToken"));

        }
        final String url = ConfigReader.getValue(Constants.ENVIRONMENT) + "/gifts/get";

        HashMap<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("temporaryToken", temporaryToken);

        try {
            play.Logger.info("Gifts.open(): calling API");
            APIRequest reque = new APIRequest(url, "application/json");
            JsonNode jsonRequest = Json.toJson(requestMap);
            F.Promise<WS.Response> responsePromise = reque.post(jsonRequest.toString());

            F.Promise<Result> resultPromise = responsePromise.map(new F.Function<WS.Response, Result>() {
                @Override
                public Result apply(WS.Response response) throws Throwable {
                    Gift gift = null;
                    Map<String, List<Product>> otherProducts = null;

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    if(response != null) {
                        JsonNode responseNode = response.asJson();
                        if(responseNode != null) {
                            JsonNode dataNode = responseNode.findPath("data");
                            JsonNode messageNode = responseNode.findPath("message");

                            if(dataNode != null) {
                                JsonNode giftsNode = dataNode.findPath("gift");
                                JsonNode otherProductsNode = dataNode.findPath("otherProducts");

                                if(response.getStatus() == Http.Status.BAD_REQUEST) {
                                    play.Logger.error("Gifts.open(): BadRequest, " + messageNode.getTextValue());
                                    return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                                }

                                if(response.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                                    play.Logger.error("Gifts.open(): InternalServerError, " + messageNode.getTextValue());
                                    throw new Exception(messageNode.getTextValue());

                                }
                                gift = objectMapper.readValue(giftsNode, Gift.class);

                                TypeReference<Map<String, List<Product>>> productTypeRef = new TypeReference<Map<String, List<Product>>>() {};
                                otherProducts = objectMapper.readValue(otherProductsNode, productTypeRef);

                            }

                        }

                    }
                    play.Logger.info("Gifts.open(): API call OK");
                    return ok(views.html.receive_choose.render(gift, gift.deliveryDetail, otherProducts));

                }
            });
            return async(resultPromise);

        }catch(Exception e) {
            play.Logger.error("Gifts.open(): Exception");
            play.Logger.error("Gifts.open(): " + temporaryToken);
            return Global.globalObject.onError(request(), e);

        }


    }

}
