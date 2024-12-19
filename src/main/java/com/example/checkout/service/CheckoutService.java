package com.example.checkout.service;
import com.example.checkout.model.Cart;
import com.example.checkout.model.Discount;
import com.example.checkout.model.Item;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {
    private final Cart cart = new Cart();

    /**
     * Adds the specified item to the cart. If the item already exists in the cart,
     * its quantity is incremented by one. Otherwise, the item is added with a quantity of one.
     */
    public void scanItem(Item item,Cart cart) {
        cart.getItems().merge(item.getId(), 1, Integer::sum);
    }



    /**
     * Calculates the total cost of items in the cart, applying bulk discounts
     * and additional discount offers based on the provided inventory and discount rules.
     *
     * returns the total cost of all items in the cart after applying bulk discounts and other defined discounts
     */
    public double calculateTotal(List<Item> inventory,List<Discount> discounts, Cart cart) {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
            Item item = findItemById(inventory, entry.getKey());
            int quantity = entry.getValue();

            if (item.getBulkQuantity() > 0 && quantity >= item.getBulkQuantity()) {
                int bulkSets = quantity / item.getBulkQuantity();
                int remainder = quantity % item.getBulkQuantity();
                total += bulkSets* item.getBulkQuantity() * item.getBulkPrice() + remainder * item.getPrice();
            } else {
                total += quantity * item.getPrice();
            }
        }
        
        for (Discount discount : discounts) {
            int countX = cart.getItems().getOrDefault(discount.getItemX(), 0);
            int countY = cart.getItems().getOrDefault(discount.getItemY(), 0);

            int applicableDiscounts = Math.min(countX, countY);
            total -= applicableDiscounts * discount.getSavings();
        }

        return total;
    }

    /**
     * Clears all items from the cart, resetting it to an empty state.
     */
    public void resetCart(Cart cart) {
        cart.reset();
    }

    /**
     * Searches for an item in the provided inventory using the specified item ID.
     * Throws an exception if the item is not found.
     * returns the item in the inventory that matches the specified ID
     */
    private Item findItemById(List<Item> inventory, String itemId) {
        return inventory.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    /**
     * Retrieves the current cart associated with the checkout service.
     * returns the current instance of the Cart containing items and their quantities.
     */
    public Cart getCart(Cart cart) {
        return cart;
    }
}