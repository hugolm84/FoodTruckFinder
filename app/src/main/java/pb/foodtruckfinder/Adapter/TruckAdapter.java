package pb.foodtruckfinder.Adapter;

/**
 * Created by hugo on 16/03/15.
 */

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
import pb.foodtruckfinder.Item.TruckItem;
import pb.foodtruckfinder.R;

/**
 * Created by hugo on 16/03/15.
 */
public class TruckAdapter extends AbstractRecycleViewAdapter<TruckItem, TruckAdapter.ViewHolder> {

    private DisplayImageOptions options;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView image;
        public final RatingBar ratingBar;
        public final ProgressBarCircularIndeterminate spinner;
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.profile_image);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            spinner = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndeterminate);
        }
    }

    public TruckAdapter(Context context, ArrayList<TruckItem> data) {
        super(context, data);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.simple_item, parent, false);
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

        holder.title.setText(get(position).getName());
        holder.ratingBar.setRating(get(position).getRating());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Position =" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, TruckDetailActivity.class);
                intent.putExtra("TRUCK_TITLE", get(position).getName());
                mContext.startActivity(intent);
            }
        });
    }
}
