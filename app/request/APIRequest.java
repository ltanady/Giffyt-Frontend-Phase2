package request;

import models.User;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import play.api.mvc.PlainResult;
import play.api.mvc.SimpleResult;
import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Tuple2;
import scala.collection.JavaConversions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ltanady
 * Date: 21/8/12
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class APIRequest {

    private WS.WSRequestHolder request = WS.url("http://localhost:9000/me/friends").setHeader("Content-Type", "application/json");

    public APIRequest(String url, String contentType) {
        this.request = WS.url(url);
        this.request.setHeader("Content-Type", contentType);

    }

    public F.Promise<WS.Response> post(String content) {
        F.Promise<WS.Response> responsePromise = this.request.post(content);

        return responsePromise;

    }


}
