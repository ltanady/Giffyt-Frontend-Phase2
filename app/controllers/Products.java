package controllers;

import global.Global;
import helper.Constants;
import models.Country;
import models.Product;
import models.User;
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

import java.util.*;

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
        /*if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Products.list(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }*/

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

    /**
     * Get list of items.
     */
    public static Result celebrateFriend(String friendId) {
        play.Logger.info("Friends.celebrateFriend(): start");
        // Get access token from cookie
        final String facebookAccessToken = session().get("facebookAccessToken");
        /*if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Friends.celebrateFriend(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        } */

        /*
        if(friendId == null) {
            play.Logger.error("Friends.celebrateFriend(): " + Messages.get("MISSING_PARAMETER", "friendId"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "friendId"));

        } */

        final String countriesUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/countries/list";
        final String friendUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/friends/get";
        final String friendsUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/friends";

        //final String productsUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/products/list";

        final HashMap<String, Object> countriesRequestMap = new HashMap<String, Object>();

        final HashMap<String, Object> friendRequestMap = new HashMap<String, Object>();
        friendRequestMap.put("facebookAccessToken", facebookAccessToken);
        friendRequestMap.put("friendId", friendId);

        final HashMap<String, Object> friendsRequestMap = new HashMap<String, Object>();
        friendsRequestMap.put("facebookAccessToken", facebookAccessToken);

        /*
        final HashMap<String, Object> productsRequestMap = new HashMap<String, Object>();
        productsRequestMap.put("facebookAccessToken", facebookAccessToken);
        productsRequestMap.put("friendId", friendId);
        productsRequestMap.put("countryCode", "sg");*/

        try {
            play.Logger.info("Friends.celebrateFriend(): calling Countries API");
            APIRequest countriesRequest = new APIRequest(countriesUrl, "application/json");
            JsonNode jsonCountriesRequest = Json.toJson(countriesRequestMap);
            F.Promise<WS.Response> countriesResponsePromise = countriesRequest.post(jsonCountriesRequest.toString());

            play.Logger.info("Friends.celebrateFriend(): calling Friend API");
            APIRequest friendRequest = new APIRequest(friendUrl, "application/json");
            JsonNode jsonFriendRequest = Json.toJson(friendRequestMap);
            F.Promise<WS.Response> friendResponsePromise = friendRequest.post(jsonFriendRequest.toString());

            play.Logger.info("Friends.celebrateFriend(): calling Friend API");
            APIRequest friendsRequest = new APIRequest(friendsUrl, "application/json");
            JsonNode jsonFriendsRequest = Json.toJson(friendsRequestMap);
            F.Promise<WS.Response> friendsResponsePromise = friendsRequest.post(jsonFriendsRequest.toString());

            /*
            play.Logger.info("Friends.celebrateFriend(): calling Products API");
            APIRequest productsRequest = new APIRequest(productsUrl, "application/json");
            JsonNode jsonProductsRequest = Json.toJson(productsRequestMap);
            F.Promise<WS.Response> productsResponsePromise = productsRequest.post(jsonProductsRequest.toString()); */

            F.Promise<Result> celebrateFriendResultPromise = F.Promise.sequence(countriesResponsePromise, friendResponsePromise, friendsResponsePromise).map(new F.Function<List<WS.Response>, Result>() {
                @Override
                public Result apply(List<WS.Response> responses) throws Throwable {
                    List<Country> countries = null;
                    User friend = null;
                    List<User> friends = null;
                    Map<String, List<Product>> products = null;

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    WS.Response countriesResponse = responses.get(0);
                    WS.Response friendResponse = responses.get(1);
                    WS.Response friendsResponse = responses.get(2);
                    //WS.Response productsResponse = responses.get(2);

                    // Got response from Events API
                    if (countriesResponse != null) {
                        play.Logger.info("Friends.celebrateFriend(): got response from Countries API");
                        JsonNode countriesResponseNode = countriesResponse.asJson();
                        JsonNode countriesNode = countriesResponseNode.findPath("data");
                        JsonNode messageNode = countriesResponseNode.findPath("message");

                        if (countriesResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.celebrateFriend(): Countries API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if (countriesResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.celebrateFriend(): Countries API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        TypeReference<List<Country>> countryTypeRef = new TypeReference<List<Country>>() {
                        };
                        countries = objectMapper.readValue(countriesNode, countryTypeRef);
                        if (countries == null) {
                            play.Logger.error("Friends.celebrateFriend(): Countries BadRequest, " + Messages.get("NULL_OBJECT", "countries"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "countries"));

                        }

                    }

                    // Got response from Friend API
                    if (friendResponse != null) {
                        play.Logger.info("Friends.celebrateFriend(): got response from Friend API");
                        JsonNode friendResponseNode = friendResponse.asJson();
                        JsonNode friendNode = friendResponseNode.findPath("data");
                        JsonNode messageNode = friendResponseNode.findPath("message");

                        if (friendResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.celebrateFriend(): Friend API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if (friendResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.celebrateFriend(): Friend API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        friend = objectMapper.readValue(friendNode, User.class);
                        /*if (friend == null) {
                            play.Logger.error("Friends.celebrateFriend(): Friend BadRequest, " + Messages.get("NULL_OBJECT", "friend"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "friend"));

                        } */

                    }

                    // Got response from Friends API
                    if (facebookAccessToken != null && friendsResponse != null) {
                        play.Logger.info("Friends.celebrateFriend(): got response from Friends API");
                        JsonNode friendsResponseNode = friendsResponse.asJson();
                        JsonNode friendsNode = friendsResponseNode.findPath("data");
                        JsonNode messageNode = friendsResponseNode.findPath("message");

                        if (friendsResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.celebrateFriend(): Friends API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if (friendsResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.celebrateFriend(): Friends API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        TypeReference<List<User>> userTypeRef = new TypeReference<List<User>>() {
                        };
                        friends = objectMapper.readValue(friendsNode, userTypeRef);
                        /*if (friends == null) {
                            play.Logger.error("Friends.celebrateFriend(): Friends BadRequest, " + Messages.get("NULL_OBJECT", "friends"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "friends"));

                        } */

                    }

                    /*
                    // Got response from Products API
                    if(productsResponse != null) {
                        play.Logger.info("Friends.celebrateFriend(): got response from Products API");
                        JsonNode productsResponseNode = productsResponse.asJson();
                        JsonNode productsNode = productsResponseNode.findPath("data");
                        JsonNode messageNode = productsResponseNode.findPath("message");

                        if(productsResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.celebrateFriend(): Products API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if(productsResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.celebrateFriend(): Products API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        TypeReference<LinkedHashMap<String, List<Product>>> productTypeRef
                                = new TypeReference<
                                LinkedHashMap<String, List<Product>>
                                >() {};
                        products = objectMapper.readValue(productsNode, productTypeRef);
                        if(countries == null) {
                            play.Logger.error("Friends.celebrateFriend(): Products BadRequest, " + Messages.get("NULL_OBJECT", "products"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "products"));

                        }

                    }*/

                    if(friend.country == null){
                        System.out.println("friend is null");
                        for(Country country: countries){
                            if(country.code.equals("SG")){
                                friend.country = country;
                                break;
                            }
                        }
                    }
                    return ok(views.html.gift.render(friend, friends, countries, products));

                }
            });
            return async(celebrateFriendResultPromise);

        }catch(Exception e) {
            play.Logger.error("Friends.celebrateFriend()(): Exception");
            play.Logger.error("Friends.celebrateFriend()(): " + facebookAccessToken);
            return Global.globalObject.onError(request(), e);

        }

    }

}
