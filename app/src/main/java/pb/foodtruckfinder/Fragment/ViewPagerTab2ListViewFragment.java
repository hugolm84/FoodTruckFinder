/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pb.foodtruckfinder.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import pb.foodtruckfinder.Adapter.MoPubAdapter;
import pb.foodtruckfinder.R;

public class ViewPagerTab2ListViewFragment extends BaseFragment {


    private AppSession mGuestAppSession;
    private MoPubAdapter adAdapter;

    private TweetViewAdapter<? extends BaseTweetView> mTweetsAdapter;
    private ObservableListView mListView;

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        mTweetsAdapter = new TweetViewAdapter<>(getActivity());
        adAdapter = new MoPubAdapter(getActivity(), mTweetsAdapter);
        mListView.setAdapter(adAdapter);

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("jFexgZBlrOo3tYoApDF9kNVjR",
                        "FCsAmEuK7yuhxQQet7xZiOAk2XjJYcsNfOOzef1QdLfnecscmI");
        Fabric.with(getActivity(), new Twitter(authConfig));

        TwitterCore.getInstance().logInGuest(new Callback() {
            @Override
            public void success(Result result) {
                mGuestAppSession = (AppSession)result.data;
                Log.d("Twitter", "Guest Loggin successfull");


                TwitterApiClient twitterApiClient =  TwitterCore.getInstance().getApiClient(mGuestAppSession);


                twitterApiClient.getStatusesService().userTimeline(null, "trushuffle", null, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        mTweetsAdapter.setTweets(listResult.data);

                        mTweetsAdapter.notifyDataSetChanged();
                        adAdapter.notifyDataSetChanged();

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
            public void failure(TwitterException exception) {
                Log.d("Twitter", exception.getMessage());
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adAdapter.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        adAdapter.loadAds();
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
