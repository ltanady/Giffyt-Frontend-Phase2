package controllers;

import formatter.GiffytDateTimeFormatter;
import global.Global;
import helper.Constants;
import models.Country;
import models.Event;
import models.Product;
import models.User;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormatter;
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

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 16/5/13
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
 */
@Security.Authenticated(Secured.class)
public class Friends extends Controller {

    public static Result eventsFriends() {
        play.Logger.info("Friends.eventsFriends(): start");

        // Get facebookAccessToken from cookie
        final String facebookAccessToken = session().get("facebookAccessToken");
        if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Friends.eventsFriends(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }

        DateTimeFormatter formatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.UK_DATE);
        final DateTime todayDate = new DateTime();
        final DateTime tomorrowDate = todayDate.plus(Days.days(1));
        final DateTime thisMonthDate = tomorrowDate.plus(Days.days(1));
        final DateTime nextMonthDate = thisMonthDate.plusMonths(1).withDayOfMonth(1);

        final String eventsUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/events";
        final String friendsUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/friends";

        final HashMap<String, String> eventsRequestMap = new HashMap<String, String>();
        eventsRequestMap.put("facebookAccessToken", facebookAccessToken);

        final HashMap<String, String> friendsRequestMap = new HashMap<String, String>();
        friendsRequestMap.put("facebookAccessToken", facebookAccessToken);

        try {
            play.Logger.info("Friends.eventsFriends(): calling Events API");
            APIRequest eventsRequest = new APIRequest(eventsUrl, "application/json");
            JsonNode jsonEventsRequest = Json.toJson(eventsRequestMap);
            F.Promise<WS.Response> eventsResponsePromise = eventsRequest.post(jsonEventsRequest.toString());

            play.Logger.info("Friends.eventsFriends(): calling Friends API");
            APIRequest friendsRequest = new APIRequest(friendsUrl, "application/json");
            JsonNode jsonFriendsRequest = Json.toJson(friendsRequestMap);
            F.Promise<WS.Response> friendsResponsePromise = friendsRequest.post(jsonFriendsRequest.toString());

            F.Promise<Result> eventsFriendsResultPromise = F.Promise.sequence(eventsResponsePromise, friendsResponsePromise).map(new F.Function<List<WS.Response>, Result>() {
                @Override
                public Result apply(List<WS.Response> responses) throws Throwable {
                    Event event = null;
                    List<User> friends = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    WS.Response eventsResponse = responses.get(0);
                    WS.Response friendsResponse = responses.get(1);

                    // Got response from events API
                    if(eventsResponse != null) {
                        play.Logger.info("Friends.eventsFriends(): got response from Events API");
                        JsonNode eventsResponseNode = eventsResponse.asJson();
                        JsonNode eventsNode = eventsResponseNode.findPath("data");
                        JsonNode messageNode = eventsResponseNode.findPath("message");

                        if(eventsResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.eventsFriends(): Events API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if(eventsResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.eventsFriends(): Events API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }

                        event = objectMapper.readValue(eventsNode, Event.class);
                        if(event == null) {
                            play.Logger.error("Friends.eventsFriends(): Events BadRequest, " + Messages.get("NULL_OBJECT", "events"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "events"));

                        }

                    }

                    // Got response from events API
                    if(friendsResponse != null) {
                        play.Logger.info("Friends.eventsFriends(): got response from Friends API");
                        JsonNode friendsResponseNode = friendsResponse.asJson();
                        JsonNode friendsNode = friendsResponseNode.findPath("data");
                        JsonNode messageNode = friendsResponseNode.findPath("message");

                        if(friendsResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.eventsFriends(): Friends API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if(friendsResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.eventsFriends(): Friends API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        TypeReference<List<User>> userTypeRef = new TypeReference<List<User>>() {};
                        friends = objectMapper.readValue(friendsNode, userTypeRef);
                        if(friends == null) {
                            play.Logger.error("Friends.eventsFriends(): Friends BadRequest, " + Messages.get("NULL_OBJECT", "friends"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "friends"));

                        }

                    }

                    Boolean tempHasNoEvents = false;
                    if(event.today.size() == 0 && event.tomorrow.size() == 0 && event.thisMonth.size() == 0 &&
                            event.nextMonth.size() == 0 )
                        tempHasNoEvents = true;

                    final Boolean hasNoEvents = tempHasNoEvents;

                    return ok(views.html.birthday.render(event, friends, todayDate, tomorrowDate, nextMonthDate, hasNoEvents));

                }
            });
            return async(eventsFriendsResultPromise);

        }catch(Exception e) {
            play.Logger.error("Friends.eventsFriends(): Exception");
            play.Logger.error("Friends.eventsFriends(): " + facebookAccessToken);
            return Global.globalObject.onError(request(), e);

        }

    }

    public static Result celebrateFriend(String friendId) {
        play.Logger.info("Friends.celebrateFriend(): start");
        // Get access token from cookie
        final String facebookAccessToken = session().get("facebookAccessToken");
        if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Friends.celebrateFriend(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }

        if(friendId == null) {
            play.Logger.error("Friends.celebrateFriend(): " + Messages.get("MISSING_PARAMETER", "friendId"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "friendId"));

        }

        final String countriesUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/countries/list";
        final String friendUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/friends/get";
        //final String productsUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/products/list";

        final HashMap<String, Object> countriesRequestMap = new HashMap<String, Object>();

        final HashMap<String, Object> friendRequestMap = new HashMap<String, Object>();
        friendRequestMap.put("facebookAccessToken", facebookAccessToken);
        friendRequestMap.put("friendId", friendId);

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

            /*
            play.Logger.info("Friends.celebrateFriend(): calling Products API");
            APIRequest productsRequest = new APIRequest(productsUrl, "application/json");
            JsonNode jsonProductsRequest = Json.toJson(productsRequestMap);
            F.Promise<WS.Response> productsResponsePromise = productsRequest.post(jsonProductsRequest.toString()); */

            F.Promise<Result> celebrateFriendResultPromise = F.Promise.sequence(countriesResponsePromise, friendResponsePromise).map(new F.Function<List<WS.Response>, Result>() {
                @Override
                public Result apply(List<WS.Response> responses) throws Throwable {
                    List<Country> countries = null;
                    User friend = null;
                    Map<String, List<Product>> products = null;

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    WS.Response countriesResponse = responses.get(0);
                    WS.Response friendResponse = responses.get(1);
                    //WS.Response productsResponse = responses.get(2);

                    // Got response from Events API
                    if(countriesResponse != null) {
                        play.Logger.info("Friends.celebrateFriend(): got response from Countries API");
                        JsonNode countriesResponseNode = countriesResponse.asJson();
                        JsonNode countriesNode = countriesResponseNode.findPath("data");
                        JsonNode messageNode = countriesResponseNode.findPath("message");

                        if(countriesResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.celebrateFriend(): Countries API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if(countriesResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.celebrateFriend(): Countries API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        TypeReference<List<Country>> countryTypeRef = new TypeReference<List<Country>>() {};
                        countries = objectMapper.readValue(countriesNode, countryTypeRef);
                        if(countries == null) {
                            play.Logger.error("Friends.celebrateFriend(): Countries BadRequest, " + Messages.get("NULL_OBJECT", "countries"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "countries"));

                        }

                    }

                    // Got response from Friend API
                    if(friendResponse != null) {
                        play.Logger.info("Friends.celebrateFriend(): got response from Friend API");
                        JsonNode friendResponseNode = friendResponse.asJson();
                        JsonNode friendNode = friendResponseNode.findPath("data");
                        JsonNode messageNode = friendResponseNode.findPath("message");

                        if(friendResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.celebrateFriend(): Friend API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if(friendResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.celebrateFriend(): Friend API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        friend = objectMapper.readValue(friendNode, User.class);
                        if(friend == null) {
                            play.Logger.error("Friends.celebrateFriend(): Friend BadRequest, " + Messages.get("NULL_OBJECT", "friend"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "friend"));

                        }

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
                    return ok(views.html.gift.render(friend, null, countries, products));

                }
            });
            return async(celebrateFriendResultPromise);

        }catch(Exception e) {
            play.Logger.error("Friends.celebrateFriend()(): Exception");
            play.Logger.error("Friends.celebrateFriend()(): " + facebookAccessToken);
            return Global.globalObject.onError(request(), e);

        }

    }

    public static Result wrapGift(String friendId, Long productId) {
        final Http.Context ctx = ctx();
        play.Logger.info("Friends.wrapGift(): start");
        final String facebookAccessToken = session().get("facebookAccessToken");
        if(facebookAccessToken == null || facebookAccessToken.isEmpty()) {
            play.Logger.error("Friends.wrapGift(): " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "facebookAccessToken"));

        }

        if(friendId == null) {
            play.Logger.error("Friends.wrapGift(): " + Messages.get("MISSING_PARAMETER", "friendId"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "friendId"));

        }

        if(productId == null) {
            play.Logger.error("Friends.wrapGift(): " + Messages.get("MISSING_PARAMETER", "productId"));
            return Global.globalObject.onBadRequest(request(), Messages.get("MISSING_PARAMETER", "productId"));

        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final String friendUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/friends/get";
        final String productUrl = ConfigReader.getValue(Constants.ENVIRONMENT) + "/products/get";

        final HashMap<String, Object> friendRequestMap = new HashMap<String, Object>();
        friendRequestMap.put("facebookAccessToken", facebookAccessToken);
        friendRequestMap.put("friendId", friendId);

        final HashMap<String, Object> productRequestMap = new HashMap<String, Object>();
        productRequestMap.put("productId", productId);

        try {

            play.Logger.info("Friends.wrapGift(): calling Friend API");
            APIRequest friendRequest = new APIRequest(friendUrl, "application/json");
            JsonNode jsonFriendRequest = Json.toJson(friendRequestMap);
            F.Promise<WS.Response> friendResponsePromise = friendRequest.post(jsonFriendRequest.toString());

            play.Logger.info("Friends.wrapGift(): calling Product API");
            APIRequest productRequest = new APIRequest(productUrl, "application/json");
            JsonNode jsonProductRequest = Json.toJson(productRequestMap);
            F.Promise<WS.Response> productResponsePromise = productRequest.post(jsonProductRequest.toString());

            F.Promise<Result> wrapGiftResultPromise = F.Promise.sequence(friendResponsePromise, productResponsePromise).map(new F.Function<List<WS.Response>, Result>() {
                @Override
                public Result apply(List<WS.Response> responses) throws Throwable {
                    User friend = null;
                    Product product = null;

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    WS.Response friendResponse = responses.get(0);
                    WS.Response productResponse = responses.get(1);

                    // Got response from Friend API
                    if(friendResponse != null) {
                        play.Logger.info("Friends.wrapGift(): got response from Friend API");
                        JsonNode friendResponseNode = friendResponse.asJson();
                        JsonNode friendNode = friendResponseNode.findPath("data");
                        JsonNode messageNode = friendResponseNode.findPath("message");

                        if(friendResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.wrapGift(): Friend API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if(friendResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.wrapGift(): Friend API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        friend = objectMapper.readValue(friendNode, User.class);
                        if(friend == null) {
                            play.Logger.error("Friends.wrapGift(): Friend BadRequest, " + Messages.get("NULL_OBJECT", "friend"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "friend"));

                        }

                    }

                    // Got response from Product API
                    if(productResponse != null) {
                        play.Logger.info("Friends.wrapGift(): got response from Product API");
                        JsonNode productResponseNode = productResponse.asJson();
                        JsonNode productNode = productResponseNode.findPath("data");
                        JsonNode messageNode = productResponseNode.findPath("message");

                        if(friendResponse.getStatus() == Http.Status.BAD_REQUEST) {
                            play.Logger.error("Friends.wrapGift(): Product API BadRequest, " + messageNode.getTextValue());
                            return Global.globalObject.onBadRequest(request(), messageNode.getTextValue());

                        }

                        if(friendResponse.getStatus() == Http.Status.INTERNAL_SERVER_ERROR) {
                            play.Logger.error("Friends.wrapGift(): Product API InternalServerError, " + messageNode.getTextValue());
                            throw new Exception(messageNode.getTextValue());

                        }
                        product = objectMapper.readValue(productNode, Product.class);
                        if(friend == null) {
                            play.Logger.error("Friends.wrapGift(): Product BadRequest, " + Messages.get("NULL_OBJECT", "product"));
                            return Global.globalObject.onBadRequest(request(), Messages.get("NULL_OBJECT", "product"));

                        }

                    }
                    return ok(views.html.send.render(friend, product));

                }

            });
            return async(wrapGiftResultPromise);


        }catch(Exception e) {
            play.Logger.error("Friends.wrapGift(): Exception");
            play.Logger.error("Friends.wrapGift(): " + facebookAccessToken);
            return Global.globalObject.onError(request(), e);

        }

    }

}
