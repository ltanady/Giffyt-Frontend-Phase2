package models;

import deserializers.GiffytDateDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 18/5/13
 * Time: 4:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gift {

    public Long id;

    public String message;

    public Product product;

    public String senderFacebookId;

    public String senderName;

    public String senderEmail;

    public String recipientFacebookId;

    public String recipientName;

    public String recipientEmail;

    public String temporaryToken;

    public Boolean isSurprise;

    public DeliveryDetail deliveryDetail;

}
