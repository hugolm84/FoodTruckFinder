package pb.foodtruckfinder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import pb.foodtruckfinder.Activity.TruckDetailActivity;
import pb.foodtruckfinder.Item.ReviewItem;
import pb.foodtruckfinder.Item.TruckItem;
import pb.foodtruckfinder.R;

/**
 * Created by hugo on 18/03/15.
 */
public class ReviewAdapter extends AbstractRecycleViewAdapter<ReviewItem, ReviewAdapter.ViewHolder> {

    private DisplayImageOptions options;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name, date, review;
        public final ImageView image;
        public final RatingBar ratingBar;
        public final ProgressBarCircularIndeterminate spinner;
        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            review = (TextView) view.findViewById(R.id.review);
            date = (TextView) view.findViewById(R.id.date);
            image = (ImageView) view.findViewById(R.id.profile_image);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            spinner = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndeterminate);
        }
    }

    public ReviewAdapter(Context context, ArrayList<ReviewItem> data) {
        super(context, data);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ImageLoader.getInstance().displayImage("drawable://" + get(position).getResource(), holder.image, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.spinner.setVisibility(View.VISIBLE);
                holder.spinner.setActivated(true);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.spinner.setVisibility(View.GONE);
                holder.spinner.setActivated(false);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.spinner.setVisibility(View.GONE);
                holder.spinner.setActivated(false);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.spinner.setVisibility(View.GONE);
                holder.spinner.setActivated(false);
            }
        });

        holder.name.setText(get(position).getName());
        holder.date.setText(get(position).getDate());
        holder.review.setText(get(position).getReview());
        holder.ratingBar.setRating(get(position).getRating());

    }
}