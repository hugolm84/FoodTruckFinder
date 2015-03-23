package pb.foodtruckfinder.Item;

/**
 * Created by hugo on 18/03/15.
 */
public class ReviewItem {
    private final String name;
    private final String review;
    private final String date;
    private final int resource;
    private final int rating;

    public ReviewItem(final String name, final String date, final String review, final int resource,
                      final int rating) {
        this.name = name;
        this.review = review;
        this.date = date;
        this.resource = resource;
        this.rating = rating;
    }

    public final String getName() {
        return name;
    }
    public final String getReview() {
        return review;
    }
    public final String getDate() {
        return date;
    }

    public final int getResource() {
        return resource;
    }
    public final int getRating(){
        return rating;
    }
}
