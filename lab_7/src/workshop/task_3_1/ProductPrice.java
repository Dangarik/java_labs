package workshop.task_3_1;

public class ProductPrice {
    private final String shopName;
    private final double price;
    private final double taxPercent;
    private final double promoDiscount;
    private double finalPrice;

    public ProductPrice(String shopName, double price, double taxPercent, double promoDiscount) {
        this.shopName = shopName;
        this.price = price;
        this.taxPercent = taxPercent;
        this.promoDiscount = promoDiscount;
    }
    public String getShopName() { return shopName; }
    public double getFinalPrice() { return finalPrice; }
    public void calculateFinalPrice() {
        // Formula specified by the workshop example.
        finalPrice = price - price * taxPercent + price * promoDiscount;
    }
    @Override public String toString() {
        return shopName + " price " + price + " final price " + finalPrice;
    }
}
