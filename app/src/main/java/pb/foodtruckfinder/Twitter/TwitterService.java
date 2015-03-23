package pb.foodtruckfinder.Twitter;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;

import pb.foodtruckfinder.Utils.NetworkUtil;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Created by hugo on 19/03/15.
 */

public class TwitterService {
    private static final int SEARCH_COUNT = 20;
    private static final String SEARCH_RESULT_TYPE = "mixed";
    private static final String SEARCH_RESULT_LANGUAGE = "en";



    /**
     * Created by Andres Escobar on 2/9/15.
     * Callback listener for TwitterGuestSessionFetcher task
     */
    public interface TwitterGuestSessionFetcherListener {

        void onTwitterGuestSessionFetcherTaskAboutToStart();
        void onTwitterGuestSessionFetcherTaskSuccessful(Session session);
    }

    public interface TwitterSearchTaskListener {

        void onTwitterSearchTaskAboutToStart();
        void onTwitterSearchTaskSuccessful(List<Tweet> tweets);
    }

    private NetworkUtil mNetworkUtil = new NetworkUtil();

    public TwitterService() {
    }

    //publicly accessible method that gets guest session from Twitter API client
    public void loginAsGuest(final TwitterGuestSessionFetcherListener twitterGuestSessionFetcherListener, Context context) throws NetworkErrorException, TwitterException {
        if (mNetworkUtil.isNetworkAvailable(context)) {
            twitterGuestSessionFetcherListener.onTwitterGuestSessionFetcherTaskAboutToStart();

            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {

                @Override
                public void success(com.twitter.sdk.android.core.Result<AppSession> result) {
                    twitterGuestSessionFetcherListener.onTwitterGuestSessionFetcherTaskSuccessful(result.data);
                }

                @Override
                public void failure(TwitterException exception) {
                    throw exception;
                }
            });
        } else {
            throw new NetworkErrorException("Not connected to network...");
        }
    }

    public void searchForTweets(final TwitterSearchTaskListener twitterSearchTaskListener, Context context,
                                Session session, String searchQuery, Long maxId) throws NetworkErrorException, TwitterException {

        if (mNetworkUtil.isNetworkAvailable(context)) {
            twitterSearchTaskListener.onTwitterSearchTaskAboutToStart();

            Twitter.getApiClient(session)
                    .getSearchService()
                    .tweets(searchQuery, null, SEARCH_RESULT_LANGUAGE, null, SEARCH_RESULT_TYPE, SEARCH_COUNT, null, null,
                            maxId, true, new Callback<Search>() {

                                @Override
                                public void success(Result<Search> searchResult) {
                                    twitterSearchTaskListener.onTwitterSearchTaskSuccessful(searchResult.data.tweets);
                                }

                                @Override
                                public void failure(TwitterException exception) {
                                    throw exception;
                                }
                            });
        } else {
            throw new NetworkErrorException("Not connected to network...");
        }
    }

}
