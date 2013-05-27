package models;

import deserializers.GiffytDateDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 16/5/13
 * Time: 2:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class User {

    public String facebookId;

    public String name;

    public String email;

    public String gender;

    @JsonDeserialize(using = GiffytDateDeserializer.class)
    public DateTime birthday;

    public Long age;

    public Country country;

}
