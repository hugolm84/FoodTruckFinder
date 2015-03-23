package pb.foodtruckfinder.Item;

/**
 * Created by hugo on 16/03/15.
 */
public class TruckItem {
    private final String name;
    private final int resource;
    private final int rating;

    public TruckItem(final String name, final int resource, final int rating) {
        this.name = name;
        this.resource = resource;
        this.rating = rating;
    }

    public final int getResource() {
        return resource;
    }

    public final String getName() {
        return name;
    }

    public final int getRating(){
        return rating;
    }
}
