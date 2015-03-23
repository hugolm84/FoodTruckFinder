package pb.foodtruckfinder.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import pb.foodtruckfinder.Item.TruckItem;
import pb.foodtruckfinder.R;

/**
 * Created by hugo on 16/03/15.
 */
abstract class AbstractRecycleViewAdapter<T, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> {

    protected final Context mContext;
    private List<T> mData;

    public AbstractRecycleViewAdapter(Context context, ArrayList<T> data) {
        mContext = context;
        mData = data;
    }

    @Override
    abstract public K onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    abstract public void onBindViewHolder(final K holder, final int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(final T item, int position) {
        position = position == -1 ? getItemCount()  : position;
        mData.add(position,item);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position < getItemCount()  ) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public final T get(int position) {
        return mData.get(position);
    }

}
