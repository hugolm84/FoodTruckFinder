package pb.foodtruckfinder.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;

import java.util.List;
import pb.foodtruckfinder.Adapter.MoPubAdapter;
import pb.foodtruckfinder.MainActivity;
import pb.foodtruckfinder.R;

/**
 * Created by hugo on 24/03/15.
 */
public class TweetsFragment  extends BaseFragment {

    private AppSession mGuestAppSession;
    private MoPubAdapter mAdAdapter;

    private TweetViewAdapter<? extends BaseTweetView> mTweetsAdapter;
    private ObservableListView mListView;

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        mTweetsAdapter = new TweetViewAdapter<>(getActivity());
        mAdAdapter = new MoPubAdapter(getActivity(), mTweetsAdapter);
        mListView.setAdapter(mAdAdapter);

        Session activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if(activeSession != null && !activeSession.getAuthToken().isExpired()) {
            getTweets("curbsidesthlm", activeSession);
            return;
        }

        TwitterCore.getInstance().logInGuest(new Callback() {
            @Override
            public void success(Result result) {
                mGuestAppSession = (AppSession)result.data;
                Log.d("Twitter", "Guest Loggin successfull");
                getTweets("curbsidesthlm", mGuestAppSession);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("Twitter", exception.getMessage());
            }
        });


    }

    protected void getTweets(final String userName, Session session) {

        TwitterApiClient twitterApiClient =  TwitterCore.getInstance().getApiClient(session);

        twitterApiClient.getStatusesService().userTimeline(null, userName, null, null, null, null, null, null, null, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> listResult) {
                mTweetsAdapter.setTweets(listResult.data);

                mTweetsAdapter.notifyDataSetChanged();
                mAdAdapter.notifyDataSetChanged();

                for(Tweet tweet : listResult.data) {
                    Log.d("TwitterResult", tweet.toString());
                }
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("Twitter", e.getMessage());

            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdAdapter.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdAdapter.loadAds();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        Activity parentActivity = getActivity();


        mListView = (ObservableListView) view.findViewById(R.id.scroll);
        mListView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            mListView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        return view;
    }
}
