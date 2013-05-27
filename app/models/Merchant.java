package models;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 15/5/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Merchant {

    public Long id;

    public String name;

    public Country country;

    public List<DeliveryAreaOption> deliveryAreaOptions;

    public List<DeliveryTimeOption> deliveryTimeOptions;

}
