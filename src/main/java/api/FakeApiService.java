package api;

import model.Discount;
import model.Order;
import model.RecipeCookie;
import model.cookie.CookieComposant;
import model.Shop;
import model.cookie.Recipe;
import model.customer.Customer;

import java.util.*;

import static api.FakeApiServiceGenerator.*;

/**
 * @author Virgile FANTAUZZI
 */


public class FakeApiService implements ApiService {

    private List<Customer> users = generateUsers();
    private Map<String, RecipeCookie> recipes = generateCookieRecipes();
    private Map<String, CookieComposant> doughs = generateCookieDough();
    private Map<String, CookieComposant> topping = generateCookieTopping();
    private Map<String, CookieComposant> mix = generateCookieMix();
    private Map<String, CookieComposant> cooking = generateCookieCooking();
    private Map<String, CookieComposant> flavours = generateCookieFlavour();
    private HashMap<Customer, ArrayList<Discount>> discounts = new HashMap<>(generateDiscounts());
    private List<Order> orders = new ArrayList<>();
    private HashMap<String, Discount> shopDiscounts= new HashMap<String, Discount>(getShopDiscounts());
    private List<Shop> shops = new ArrayList<>();


    /**
     * Return a list of {@link Customer}
     * Those users must be generated by {@link FakeApiServiceGenerator}
     */
    @Override
    public List<Customer> getUsers() {
        return users;
    }

    /**
     * Return a map of strings and {@link Recipe}
     * Those cookies must be generated by {@link FakeApiServiceGenerator}
     */
    @Override
    public Map<String, RecipeCookie> getCookieRecipes() {
        return recipes;
    }

    @Override
    public Map<String, CookieComposant> getCookieDough() {
        return doughs;
    }

    @Override
    public Map<String, CookieComposant> getCookieTopping() {
        return topping;
    }

    @Override
    public Map<String, CookieComposant> getCookieMix() {
        return mix;
    }

    @Override
    public Map<String, CookieComposant> getCookieCooking() {
        return cooking;
    }

    @Override
    public Map<String, CookieComposant> getCookieFlavour() {
        return flavours;
    }


    @Override
    public void addDiscount(Customer customer, Discount discount) {
        if (customer.isRegistered()) {
            if (discounts.containsKey(customer)) {
               // ArrayList<Discount> dis = discounts.get(customer);
                //discounts.put(customer,)
                discounts.get(customer).add(discount);
            } else {
                discounts.put(customer, new ArrayList<>(Collections.singletonList(discount)));
            }
        }
    }

    @Override
    public List<Discount> getDiscounts(Customer customer) {
        if(customer.isRegistered()) return discounts.get(customer);

        else {
            System.out.println("Customer not registered");
            return null;
        }

    }

    /**
     * Used to apply a discount asked by a customer
     * If the customer is not registered, return the total price of his cart
     * @param customer
     * @param discount
     * @return
     */
    @Override
    public double applyDiscount(Customer customer, Discount discount) {
        if(customer.isRegistered()) {
            if (discounts.containsKey(customer)) {
                try {
                    discounts.get(customer).remove(discount);
                    return (customer.getCart().getTotalPrice()) * (1.f - discount.getRate());
                }
                catch (Throwable e){
                    System.out.println("You don't have the right to this discount ");
                }

            }
        }
        return customer.getCart().getTotalPrice();
    }

    /**
     * the customer may just need to see the reduction which may be applied
     * Not useful right now
     * @param customer
     * @param discount
     * @return
     */
    @Override
    public float askForADiscountApplying(Customer customer, Discount discount){
        if(customer.isRegistered()) {
            return discount.getRate();
        }

        return 0.0f;

    }

    // public float getFinalPrice(Customer customer, Discount discount){ }

    /**
     * Return a list of {@link Order}
     * Those orders must be generated by {@link FakeApiServiceGenerator} if you want some MOCK
     */
    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public List<Shop> getShops() {
        return shops;
    }

    /**
     * Generate an order ID according to the size of orders {@link Order} .
     * This number must be only get here.
     */
    @Override
    public int getOrderNum() {
        return orders.size();
    }

    /**
     * Generate a random {@link Customer} and add it {@link FakeApiService#users} list.
     * This user must be get from the {@link FakeApiServiceGenerator#FAKE_USERS_RANDOM} list.
     */
    @Override
    public void generateRandomUser() {
        users.add(Customer.random());
    }

    /**
     * Delete a {@link Customer} from the {@link FakeApiService#users} list.
     */
    @Override
    public void deleteUser(Customer user) {
        users.remove(user);
    }

    /**
     * Add a {@link Customer} from the {@link FakeApiService#users} list.
     */
    @Override
    public void addUser(Customer user) {
        users.add(user);
    }

    /**
     * Delete a {@link Order} from the {@link FakeApiService#users} list.
     */
    @Override
    public void deleteOrder(Order order) {
        orders.remove(order);

    }

    /**
     * Add a {@link Order} from the {@link FakeApiService#users} list.
     */
    @Override
    public void addOrder(Order order) {
        giveDiscount(order);
        order.setOrderAmount(order.getCart().getTotalPrice());
        orders.add(order);
        System.out.println(order.toString());
        order.getCustomer().emptyCart(); // and empty the model.customer's cart

    }

    /**
     * used to get an order with a discount
     * @param order
     * @param discount
     */
    @Override
    public void addOrder(Order order, Discount discount) {
        giveDiscount(order);
        System.out.println("The discount "+ discount.toString() + " have been used!");
        order.setOrderAmount(applyDiscount(order.getCustomer(), discount));
        System.out.println(order.toString());
        orders.add(order);
        order.getCustomer().emptyCart(); // and empty the model.customer's cart

    }

    /**
     * Add a {@link Shop } from the {@link FakeApiService#shops} list.
     * @param shop
     */
    @Override
    public void addShop(Shop shop) {
        shops.add(shop);
        System.out.println(shop.getShopName() + ",nouveau Shop ajouté ");
    }

    /**
     * Delete a {@link Shop } from the {@link FakeApiService#shops} list.
     * @param shop
     */
    @Override
    public void deleteShop(Shop shop) {
        shops.remove(shop);
    }


    /**
     * change schedule of a {@link Shop }
     * @param shop
     * @param open
     * @param close
     */
    @Override
    public void changeHorairesShop(Shop shop, Date open, Date close) {
        shop.setOpenShop(open);
        shop.setCloseShop(close);
        System.out.println("Le shop: " + shop.getShopName() + " sera ouvert de" + shop.getOpenShop() + " à "+ shop.getCloseShop());
    }

    /**
     * used to give some discount to a customer for things he has bought
     * @return the final price of the order
     */
    public void giveDiscount(Order order){

        int cookiesNumber=order.getCart().getCookiesNumber();
        if (order.getCustomer().isRegistered() && cookiesNumber>=30){
           addDiscount(order.getCustomer(), shopDiscounts.get("LOYALTY_PROGRAM"));
           System.out.println("Great news! you get the Loyalty_program discount (10% discount). Use it next time)");
        }



    }
}
