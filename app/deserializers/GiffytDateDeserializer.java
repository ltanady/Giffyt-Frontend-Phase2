package deserializers;

import formatter.GiffytDateTimeFormatter;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * User: ltanady
 * Date: 21/8/12
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class GiffytDateDeserializer extends JsonDeserializer<DateTime> {

    public DateTime deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {

        String date = jsonparser.getText();
        if(date != null) {
            DateTimeFormatter dateTimeFormatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.UK_DATE_NO_YEAR);
            return dateTimeFormatter.parseDateTime(date);

        }
        return null;

    }

}
