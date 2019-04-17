package com.example.stackapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.stackapp.models.Tag;
import com.example.stackapp.network.RemoteCallsInterface;
import com.example.stackapp.network.RetrofitInstance;
import com.example.stackapp.utils.Consts;
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchConfirmedListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchQueryChangeListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener;


import com.example.stackapp.utils.VerticalSpacingItemDecorator;
import com.example.stackapp.utils.extension.ResourceHelper;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.utils.SuggestionCreationUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static List<Question> sData = new ArrayList<>();
    private String [] sortOptions = {Consts.SORT_BY_ACTIVITY,
            Consts.SORT_BY_CREATION,
            Consts.SORT_BY_VOTES};
    private static String sCurrentQuery;
    private PopupMenu mPopupMenu;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    public PersistentSearchView persistentSearchView;
    private List<String> mInitialTagNames;
    private QuestionAdapter mQuestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup UI
        initProgressBar();
        initSearchView();
        initEmptyView();
        initRecyclerView();


    }

    private void initProgressBar(){
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
    }


    private void initRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(
                ResourceHelper.dpToPx(2),
                ResourceHelper.dpToPx(2)));

        if(sData ==null || sData.isEmpty()){
            mQuestionAdapter = new QuestionAdapter(new ArrayList<>());
        } else{
            mQuestionAdapter = new QuestionAdapter(sData);
        }


        mRecyclerView.setAdapter(mQuestionAdapter);


    }

    // private helper method to get data from network using Retrofit
    private void getSearchData(String tag, String sort){


        RemoteCallsInterface service = RetrofitInstance.getRetrofit()
                .create(RemoteCallsInterface.class);

        Call<QuestionList> listQues = service.getQuestions(sort, tag, Consts.SITE);

        listQues.enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                // User feedback
                persistentSearchView.hideProgressBar(false);
                persistentSearchView.showLeftButton();

                sData = response.body().getQuestionList();
                mQuestionAdapter.updateItems(sData);

                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.animate()
                        .alpha(1f)
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(300L)
                        .start();

            }

            @Override
            public void onFailure(Call<QuestionList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong! Try again later.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();

            }
        });
        


    }

    private void initEmptyView(){
        mEmptyView = findViewById(R.id.emptyViewLl);

        if(!sData.isEmpty()){

            mEmptyView.setVisibility(
                    (sData.isEmpty() ? View.VISIBLE : View.GONE)
            );


        }


    }


    private void initSearchView(){
        persistentSearchView = findViewById(R.id.persistentSearchView);


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
    private void loadTagData(){
        GetSuggestedTagsAsyncTask asyncTask = new GetSuggestedTagsAsyncTask( TagDatabase.
                getInstance(this.getApplicationContext()).tagDao());
        asyncTask.execute();
    }

    // sets suggestionList to persistentSearchView
    private void setSuggestionTagsList(List<String> tagNames, boolean expandIfNecessary){

        persistentSearchView.setSuggestions(SuggestionCreationUtil.
                asRegularSearchSuggestions(tagNames),
                expandIfNecessary);

    }



    // helper method to perform search operation
    private void performSearch(String query, String sort){

        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setAlpha(0f);
        mProgressBar.setVisibility(View.VISIBLE);
        mQuestionAdapter.clear();

        Runnable searchTask = () -> getSearchData(query,sort);

        new Handler().post(searchTask);


        persistentSearchView.hideLeftButton(false);
        persistentSearchView.showProgressBar();



    }


    // This method filters initial suggestion tags based on user input
    private List<String> getSuggestionForQuery(String query){
        List<String> pickedSuggestions = new ArrayList<>();

        if(query.isEmpty()){
            pickedSuggestions.addAll(mInitialTagNames);
        } else {
            for(String tagName : mInitialTagNames){
                if(tagName.toLowerCase().startsWith(query.toLowerCase())){
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

        switch (id){

            case R.id.leftBtnIv:

                // handles sorting button clicks
                if(sCurrentQuery !=null && !sCurrentQuery.isEmpty()){
                    showSortPopupMenu(v);
                }

                break;
            case R.id.clearInputBtnIv:
                // clear Button function
                clearButtonBehaviour();
                break;
            case R.id.rightBtnIv:
                // filterButton
                filterSearchResult();
                break;

        }


    }

    // private helper method to show sortBy popup menu
    private void showSortPopupMenu(View view){
        if(mPopupMenu == null){
            mPopupMenu = new PopupMenu(this, view);
            MenuInflater menuInflater = mPopupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.sort_actions, mPopupMenu.getMenu());
        }

        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){


                    case R.id.sort_activity:
                        performSearch(sCurrentQuery,sortOptions[0]);
                        break;

                    case R.id.sort_creation:
                        performSearch(sCurrentQuery,sortOptions[1]);
                        break;

                    case R.id.sort_votes:
                        performSearch(sCurrentQuery, sortOptions[2]);
                        break;

                }

                //popupMenu.dismiss();
                return true;
            }
        });


        mPopupMenu.show();

    }

    private void filterSearchResult() {
        /* TODO: imlement filter search result */
        Toast.makeText(this, "Search Filter is on!", Toast.LENGTH_SHORT).show();

    }

    private void clearButtonBehaviour() {
        /* Todo: implement clear button*/
    }




    /* Listeners callbacks */

    // called when user had entered the query
    private OnSearchConfirmedListener mOnSearchConfirmedListener = new OnSearchConfirmedListener() {
        @Override
        public void onSearchConfirmed(PersistentSearchView searchView, String query) {
            // do search operation here

            searchView.collapse();
            sCurrentQuery = query;
            performSearch(query, sortOptions[0]);


        }
    };

    // called when Search Query Changes
    private OnSearchQueryChangeListener mOnSearchQueryChangeListener = new OnSearchQueryChangeListener() {
        @Override
        public void onSearchQueryChanged(PersistentSearchView searchView, String oldQuery, String newQuery) {

           if(newQuery.isEmpty()){
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

            sCurrentQuery = query;
            performSearch(query,sortOptions[0]);
        }

        @Override
        public void onSuggestionRemoved(SuggestionItem suggestion) {

        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        // calling loadTagData
        loadTagData();

    }

    @Override
    protected void onResume() {
        super.onResume();


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

        if(persistentSearchView.isExpanded()) {
            persistentSearchView.collapse();
            return;
        }


        super.onBackPressed();

    }

    // Reads tags from Database and sets to mInitialTagNames field
    class GetSuggestedTagsAsyncTask extends AsyncTask<Void, Void, List<Tag>>{
        TagDao dao;

        GetSuggestedTagsAsyncTask(TagDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<Tag> doInBackground(Void... voids) {

            if(mInitialTagNames == null){

                return  this.dao.getAllTags();

            }
            return null;


        }

        @Override
        protected void onPostExecute(List<Tag> tagList) {
            super.onPostExecute(tagList);
            mInitialTagNames = new ArrayList<>();

            if(tagList != null && tagList.size() > 0){

                // convert into List<String>
                for (Tag tag:tagList) {
                    mInitialTagNames.add(tag.getName());

                }

            }

            setSuggestionTagsList(mInitialTagNames, false);


        }
    }
}
