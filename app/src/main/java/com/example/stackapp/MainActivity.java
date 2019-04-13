package com.example.stackapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.stackapp.data.TagDao;
import com.example.stackapp.data.TagDatabase;
import com.example.stackapp.models.Tag;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    public PersistentSearchView persistentSearchView;
    private List<String> mInitialTagNames;

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

        // TODO : Attach adapter once Adapter class is created

    }

    private void initEmptyView(){
        mEmptyView = findViewById(R.id.emptyViewLl);
        // TODO: make changes when data is loaded from remote


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
    private void performSearch(String query){
        // Todo: implement search operation

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
                // sorting button
                sortSearchResult();
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

    private void filterSearchResult() {
        /* TODO: imlement filter search result */
        Toast.makeText(this, "Filter button clicked", Toast.LENGTH_SHORT).show();
    }

    private void clearButtonBehaviour() {
        /* Todo: implement clear button*/
    }


    private void sortSearchResult() {
        /* TODO: imlement sort search result */
        Toast.makeText(this, "sort button clicked", Toast.LENGTH_SHORT).show();


    }

    /* Listeners callbacks */

    // called when user had entered the query
    private OnSearchConfirmedListener mOnSearchConfirmedListener = new OnSearchConfirmedListener() {
        @Override
        public void onSearchConfirmed(PersistentSearchView searchView, String query) {
            // do search operation here
            searchView.collapse();
            performSearch(query);


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

            performSearch(query);
        }

        @Override
        public void onSuggestionRemoved(SuggestionItem suggestion) {

        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        // calling loadTagData
        loadTagData();

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

                setSuggestionTagsList(mInitialTagNames, false);

            }


        }
    }
}
