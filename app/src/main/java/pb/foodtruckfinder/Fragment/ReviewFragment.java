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

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.melnykov.fab.FloatingActionButton;

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
public class ReviewFragment extends BaseFragment {

    private static final String TAG = "ReviewFragment";

    private ObservableRecyclerView mRecyclerView;


    public static ReviewFragment newInstance() {
        ReviewFragment fragment = new ReviewFragment();
        return fragment;
    }

    public ReviewFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        ArrayList<ReviewItem> adapterData = new ArrayList<ReviewItem>() {
            {
                add(new ReviewItem("Hugo Lindström", "2015-03-22 14:02", getResources().getString(R.string.lipsum_small), R.drawable.avatar, 3));
                add(new ReviewItem("Christian Heljeved", "2015-03-22 13:02", getResources().getString(R.string.lipsum_mini), R.drawable.bunbun_prof, 3));
                add(new ReviewItem("Kristoffer Gutebrant", "2015-03-21 11:02", getResources().getString(R.string.lipsum_mini), R.drawable.funky_prof, 3));
                add(new ReviewItem("Max Lindström", "2015-03-20 12:02", getResources().getString(R.string.lipsum_mini), R.drawable.chilibussen_card, 3));
            }
        };
        ReviewAdapter adapter = new ReviewAdapter(getActivity(), adapterData);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        //mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);


        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        Fragment parentFragment = getParentFragment();
        if(parentFragment != null) {
            ViewGroup viewGroup = (ViewGroup) parentFragment.getView();
            if (viewGroup != null) {
                mRecyclerView.setTouchInterceptionViewGroup((ViewGroup) viewGroup.findViewById(R.id.container));
                if (parentFragment instanceof ObservableScrollViewCallbacks) {
                    mRecyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentFragment);
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);

        return view;
    }
}
