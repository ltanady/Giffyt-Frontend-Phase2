package models;

import deserializers.GiffytDateDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 14/3/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Campaign {

    public Long id;

    public Country country;

    public String name;

    public String description;

    public Long campaignLimit;

    @JsonDeserialize(using = GiffytDateDeserializer.class)
    public DateTime startDate;

    @JsonDeserialize(using = GiffytDateDeserializer.class)
    public DateTime endDate;

}
