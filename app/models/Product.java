package models;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 15/5/13
 * Time: 11:13 PM
 * To change this template use File | Settings | File Templates.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StockProduct.class, name = "StockProduct")
})
public abstract class Product {

    public Long id;

    public String code;

    public Country country;

    public Double price;

    public String brand;

    public Merchant merchant;

    public Campaign campaign;

    public String type;

    public List<ProductAttribute> productAttributes;

    public List<ProductImage> productImages;

}
