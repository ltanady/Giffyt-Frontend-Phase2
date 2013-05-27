package models;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 20/5/13
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryDetail {

    public String recipientName;

    public String address;

    public String city;

    public String state;

    public Country country;

    public String postalCode;

    public String contactNumber;

    public DateTime shipmentDate;

    public DeliveryDetail(){}

    public DeliveryDetail(String recipientName, String address, String city, String state, String postalCode, String contactNumber){
        this.recipientName = recipientName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.contactNumber = contactNumber;

    }

}
