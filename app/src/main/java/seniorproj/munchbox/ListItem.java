package seniorproj.munchbox;

/**
 * Created by Eric on 2/23/2018.
 */

public class ListItem {

    private String nameOfDish;
    private String restaurantName;

    public ListItem(String nameOfDish, String restaurantName) {
        this.nameOfDish = restaurantName;
        this.restaurantName = nameOfDish;
    }

    public String getNameOfDish() {
        return nameOfDish;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
}
