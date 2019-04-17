package com.example.stackapp;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.stackapp.adapter.QuestionAdapter;
import com.example.stackapp.data.TagDao;
import com.example.stackapp.data.TagDatabase;
import com.example.stackapp.models.Question;
import com.example.stackapp.models.QuestionList;
import com.example.stackapp.models.SearchModes;
import com.example.stackapp.models.Tag;
import com.example.stackapp.network.RemoteCallsInterface;
import com.example.stackapp.network.RetrofitInstance;
import com.example.stackapp.utils.Consts;
import com.example.stackapp.utils.VerticalSpacingItemDecorator;
import com.example.stackapp.utils.extension.ResourceHelper;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchConfirmedListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchQueryChangeListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener;
import com.paulrybitskyi.persistentsearchview.utils.SuggestionCreationUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public PersistentSearchView persistentSearchView;
    private SearchModes mMode = SearchModes.QUERY_SEARCH_RESULT;
    private List<Question> mData = new ArrayList<>();
    private String mCurrentQuery;
    private PopupMenu mPopupMenu;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private String[] sortOptions = {Consts.SORT_BY_ACTIVITY,
            Consts.SORT_BY_CREATION,
            Consts.SORT_BY_VOTES};
    private List<String> mInitialTagNames;
    private QuestionAdapter mQuestionAdapter;
    // called when user had entered the query
    private OnSearchConfirmedListener mOnSearchConfirmedListener = new OnSearchConfirmedListener() {
        @Override
        public void onSearchConfirmed(PersistentSearchView searchView, String query) {

            if (mMode == SearchModes.FILTER_SEARCH_RESULT) {
                filterSearchResult(query);
                searchView.collapse();
                return;
            }

            // do search query operation here
            searchView.collapse();
            mCurrentQuery = query;
            performSearch(query, sortOptions[0]);


        }
    };
    // called when Search Query Changes
    private OnSearchQueryChangeListener mOnSearchQueryChangeListener = new OnSearchQueryChangeListener() {
        @Override
        public void onSearchQueryChanged(PersistentSearchView searchView, String oldQuery, String newQuery) {

            if (mMode == SearchModes.FILTER_SEARCH_RESULT) {
                filterSearchResult(newQuery);
                return;
            }

            if (newQuery.isEmpty()) {
                setSuggestionTagsList(mInitialTagNames, true);
            } else {
                setSuggestionTagsList(getSuggestionForQuery(newQuery), true);
            }
        }
    };
    private OnSuggestionChangeListener mOnSuggestionChangeListener = new OnSuggestionChangeListener() {

        @Override
        public void onSuggestionPicked(SuggestionItem suggestion) {
            // Handle a suggestion pick event. This is the place where you'd
            // want to perform a search against your data provider.
            String query = suggestion.getItemModel().getText();

            setSuggestionTagsList(getSuggestionForQuery(query), false);

            mCurrentQuery = query;
            performSearch(query, sortOptions[0]);
        }

        @Override
        public void onSuggestionRemoved(SuggestionItem suggestion) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onRestoreInstanceState(savedInstanceState);

        //setup UI
        initProgressBar();
        initSearchView();
        initEmptyView();
        initRecyclerView();


    }

    private void initProgressBar() {
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(
                ResourceHelper.dpToPx(2),
                ResourceHelper.dpToPx(2)));

        if (mData == null || mData.isEmpty()) {
            mQuestionAdapter = new QuestionAdapter(new ArrayList<>());
        } else {
            mQuestionAdapter = new QuestionAdapter(mData);
        }


        mRecyclerView.setAdapter(mQuestionAdapter);


    }

    // private helper method to get data from network using Retrofit
    private void getSearchData(String tag, String sort) {


        RemoteCallsInterface service = RetrofitInstance.getRetrofit()
                .create(RemoteCallsInterface.class);

        Call<QuestionList> listQues = service.getQuestions(sort, tag, Consts.SITE);

        listQues.enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                // User feedback
                persistentSearchView.hideProgressBar(false);
                persistentSearchView.showLeftButton();

                mData = response.body().getQuestionList();

                if (mData.isEmpty()) {
                    showToastMessage("No results found!, please try again.");
                }

                mQuestionAdapter.updateItems(mData);
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.animate()
                        .alpha(1f)
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(300L)
                        .start();

            }

            @Override
            public void onFailure(Call<QuestionList> call, Throwable t) {
                showToastMessage("Something went wrong! Try again later.");
                t.printStackTrace();

            }
        });


    }

    private void initEmptyView() {
        mEmptyView = findViewById(R.id.emptyViewLl);

        if (!mData.isEmpty()) {

            mEmptyView.setVisibility(
                    (mData.isEmpty() ? View.VISIBLE : View.GONE)
            );


        }


    }

    private void initSearchView() {

        persistentSearchView = findViewById(R.id.persistentSearchView);

        if (mCurrentQuery != null && !mCurrentQuery.isEmpty()) {
            if (mMode != SearchModes.FILTER_SEARCH_RESULT) {
                persistentSearchView.setInputQuery(mCurrentQuery);
            }

        }


        if (mMode == SearchModes.FILTER_SEARCH_RESULT) {
            Drawable drawable = getDrawable(R.drawable.ic_filter_list_24dp);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(MainActivity.this,
                    R.color.filter_drawable_color));
            persistentSearchView.setRightButtonDrawable(drawable);
        }

        // hide or show button
        persistentSearchView.showRightButton();

        // attach button listeners here
        persistentSearchView.setOnLeftBtnClickListener(this);
        persistentSearchView.setOnRightBtnClickListener(this);
        persistentSearchView.setOnClearInputBtnClickListener(this);

        // enable/disable required feature
        persistentSearchView.setVoiceInputButtonEnabled(false);
        persistentSearchView.setClearInputButtonEnabled(true);
        persistentSearchView.setProgressBarEnabled(true);


        // attach onSearch listeners
        persistentSearchView.setOnSearchConfirmedListener(mOnSearchConfirmedListener);
        persistentSearchView.setOnSearchQueryChangeListener(mOnSearchQueryChangeListener);
        persistentSearchView.setOnSuggestionChangeListener(mOnSuggestionChangeListener);

        // User feedback options
        persistentSearchView.setDimBackground(true);
        persistentSearchView.setDismissOnTouchOutside(true);
        persistentSearchView.setSuggestionsDisabled(false);
        persistentSearchView.setQueryInputGravity(Gravity.START | Gravity.CENTER);


    }

    // private helper method to load tag data as suggestions
    private void loadTagData() {
        GetSuggestedTagsAsyncTask asyncTask = new GetSuggestedTagsAsyncTask(TagDatabase.
                getInstance(this.getApplicationContext()).tagDao());
        asyncTask.execute();
    }

    // sets suggestionList to persistentSearchView
    private void setSuggestionTagsList(List<String> tagNames, boolean expandIfNecessary) {

        if (mMode == SearchModes.FILTER_SEARCH_RESULT) {
            return;
        }

        persistentSearchView.setSuggestions(SuggestionCreationUtil.
                        asRegularSearchSuggestions(tagNames),
                expandIfNecessary);

    }

    // helper method to perform search operation
    private void performSearch(String query, String sort) {

        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setAlpha(0f);
        mProgressBar.setVisibility(View.VISIBLE);
        mQuestionAdapter.clear();

        Runnable searchTask = () -> getSearchData(query, sort);

        new Handler().post(searchTask);


        persistentSearchView.hideLeftButton(false);
        persistentSearchView.showProgressBar();


    }

    // This method filters initial suggestion tags based on user input
    private List<String> getSuggestionForQuery(String query) {
        List<String> pickedSuggestions = new ArrayList<>();

        if (query.isEmpty()) {
            pickedSuggestions.addAll(mInitialTagNames);
        } else {
            for (String tagName : mInitialTagNames) {
                if (tagName.toLowerCase().startsWith(query.toLowerCase())) {
                    pickedSuggestions.add(tagName);
                }
            }
        }

        return pickedSuggestions;

    }

    // handles onClick on searchView buttons
    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.leftBtnIv:

                // handles sorting button clicks
                if (mCurrentQuery != null && !mCurrentQuery.isEmpty()) {
                    showSortPopupMenu(v);
                }

                break;

            case R.id.clearInputBtnIv:
                // clear Button function
                break;

            case R.id.rightBtnIv:
                // filterButton
                filterButtonBehaviour();
                break;

        }


    }

    // private helper method to show sortBy popup menu
    private void showSortPopupMenu(View view) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(this, view);
            MenuInflater menuInflater = mPopupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.sort_actions, mPopupMenu.getMenu());
        }

        mPopupMenu.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {


                case R.id.sort_activity:
                    performSearch(mCurrentQuery, sortOptions[0]);
                    break;

                case R.id.sort_creation:
                    performSearch(mCurrentQuery, sortOptions[1]);
                    break;

                case R.id.sort_votes:
                    performSearch(mCurrentQuery, sortOptions[2]);
                    break;

            }

            //popupMenu.dismiss();
            return true;
        });


        mPopupMenu.show();

    }




    /* Listeners callbacks */

    // private helper method to filter search result
    private void filterSearchResult(String filterQuery) {
        List<Question> filteredResult = new ArrayList<>();

        for (Question q : mData) {

            // filtering on the basis of question's title
            if (q.getTitle().toLowerCase().contains(filterQuery.toLowerCase().trim())) {
                filteredResult.add(q);
            } else if (q.getOwner().getDisplay_name().toLowerCase().contains(filterQuery.toLowerCase())) {
                // filtering on the basis of question's owner username
                filteredResult.add(q);
            }


        }

        mQuestionAdapter.updateFilterResults(filteredResult);


    }

    // this method defines filter button behaviour
    private void filterButtonBehaviour() {

        Drawable drawable = getDrawable(R.drawable.ic_filter_list_24dp);

        if (mData != null && !mData.isEmpty()) {

            if (mMode == SearchModes.FILTER_SEARCH_RESULT) {
                // handles Search filter off behaviour

                showToastMessage("Search Filter is off!");

                DrawableCompat.setTint(drawable, ContextCompat.getColor(MainActivity.this,
                        R.color.iconColor));


                persistentSearchView.setRightButtonDrawable(drawable);
                mMode = SearchModes.QUERY_SEARCH_RESULT;
                persistentSearchView.setSuggestionsDisabled(false);
                setSuggestionTagsList(mInitialTagNames, false);

            } else {
                //handles Search filter On behaviour
                DrawableCompat.setTint(drawable, ContextCompat.getColor(MainActivity.this,
                        R.color.filter_drawable_color));
                mMode = SearchModes.FILTER_SEARCH_RESULT;

                showToastMessage("Search Filter is on!");

                persistentSearchView.collapse();
                persistentSearchView.setRightButtonDrawable(drawable);
                persistentSearchView.setInputQuery("");
                persistentSearchView.setSuggestionsDisabled(true);

            }


        }
    }

    // private helper method to show Toast message
    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // calling loadTagData
        loadTagData();


        if ((persistentSearchView.isInputQueryEmpty() && (mQuestionAdapter.getItemCount() == 0) ||
                persistentSearchView.isExpanded())) {

            persistentSearchView.expand(false);

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        } else {

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }


    }

    @Override
    public void onBackPressed() {

        if (persistentSearchView.isExpanded()) {
            persistentSearchView.collapse();
            return;
        }


        super.onBackPressed();

    }

    // restore data state
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mMode = ((SearchModes) savedInstanceState.getSerializable(Consts.SAVED_STATE_MODE));
            mData = ((List<Question>) savedInstanceState.getSerializable(Consts.SAVED_STATE_DATA));
            mCurrentQuery = savedInstanceState.getString(Consts.SAVED_STATE_QUERY);

        }
    }


    // save data state on configuration change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Consts.SAVED_STATE_MODE, mMode);
        outState.putSerializable(Consts.SAVED_STATE_DATA, (Serializable) mData);
        outState.putString(Consts.SAVED_STATE_QUERY, mCurrentQuery);
    }

    // Reads tags from Database and sets to mInitialTagNames field
    class GetSuggestedTagsAsyncTask extends AsyncTask<Void, Void, List<Tag>> {
        TagDao dao;

        GetSuggestedTagsAsyncTask(TagDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<Tag> doInBackground(Void... voids) {

            if (mInitialTagNames == null) {

                return this.dao.getAllTags();

            }
            return null;


        }

        @Override
        protected void onPostExecute(List<Tag> tagList) {
            super.onPostExecute(tagList);
            mInitialTagNames = new ArrayList<>();

            if (tagList != null && tagList.size() > 0) {

                // convert into List<String>
                for (Tag tag : tagList) {
                    mInitialTagNames.add(tag.getName());

                }

            }

            setSuggestionTagsList(mInitialTagNames, false);


        }
    }
}
