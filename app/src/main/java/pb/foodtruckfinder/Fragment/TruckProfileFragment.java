package pb.foodtruckfinder.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;
import java.util.List;

import pb.foodtruckfinder.Adapter.DividerItemDecoration;
import pb.foodtruckfinder.Adapter.ReviewAdapter;
import pb.foodtruckfinder.Adapter.SimpleRecyclerAdapter;
import pb.foodtruckfinder.Item.ReviewItem;
import pb.foodtruckfinder.Item.TruckItem;
import pb.foodtruckfinder.R;

/**
 * Created by hugo on 18/03/15.
 */
public class TruckProfileFragment extends BaseFragment {

    private static final String TAG = "TruckProfileFragment";

    public static TruckProfileFragment newInstance() {
        TruckProfileFragment fragment = new TruckProfileFragment();
        return fragment;
    }

    public TruckProfileFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truck_profile, container, false);

        View locationView = view.findViewById(R.id.location);
        View ratingView = view.findViewById(R.id.rating);

        ImageView locLImage = (ImageView)locationView.findViewById(R.id.leftImage);
        ImageView locRImage = (ImageView)locationView.findViewById(R.id.rightImage);
        locLImage.setImageResource(R.drawable.ic_location_on_grey600_24dp);
        locRImage.setImageResource(R.drawable.ic_map_grey600_24dp);
        TextView locTitle = (TextView)locationView.findViewById(R.id.title);
        locTitle.setText("Västerlånggatan 24, Stockholm");
        TextView locSubTitle = (TextView)locationView.findViewById(R.id.subtitle);
        locSubTitle.setText("2014-03-22@14:30");


        ImageView rateLImage = (ImageView)ratingView.findViewById(R .id.leftImage);
        ImageView rateRImage = (ImageView)ratingView.findViewById(R.id.rightImage);
        rateLImage.setImageResource(R.drawable.ic_star_rate_grey600_24dp);
        rateRImage.setImageResource(R.drawable.ic_rate_review_grey600_24dp);
        TextView rateTitle = (TextView)ratingView.findViewById(R.id.title);
        rateTitle.setText("Omdöme");
        TextView rateSubTitle = (TextView)ratingView.findViewById(R.id.subtitle);
        rateSubTitle.setText("430 stjärnor");

        final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        Fragment parentFragment = getParentFragment();
        if(parentFragment != null) {
            ViewGroup viewGroup = (ViewGroup) parentFragment.getView();
            if (viewGroup != null) {
                scrollView.setTouchInterceptionViewGroup((ViewGroup) viewGroup.findViewById(R.id.container));
                if (parentFragment instanceof ObservableScrollViewCallbacks) {
                    scrollView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentFragment);
                }
            }
        }
        return view;
    }
}
