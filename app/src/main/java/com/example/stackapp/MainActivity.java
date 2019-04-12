package com.example.stackapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchConfirmedListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchQueryChangeListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener;


import com.example.stackapp.utils.VerticalSpacingItemDecorator;
import com.example.stackapp.utils.extension.ResourceHelper;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.model.Suggestion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    public PersistentSearchView persistentSearchView;

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
        // TODO: make changes when data is loaded


    }


    private void initSearchView(){
        persistentSearchView = findViewById(R.id.persistentSearchView);

        // hide or show button
        persistentSearchView.showRightButton();

        // attach listeners
        persistentSearchView.setOnSearchConfirmedListener(mOnSearchConfirmedListener);
        persistentSearchView.setOnSearchQueryChangeListener(mOnSearchQueryChangeListener);
        persistentSearchView.setOnSuggestionChangeListener(mOnSuggestionChangeListener);

        // User feedback
        persistentSearchView.setDimBackground(true);
        persistentSearchView.setDismissOnTouchOutside(true);
        persistentSearchView.setSuggestionsDisabled(false);




    }

    
    // private helper method to load tag data as suggestion 
    private void loadTagData(){
        // TODO: 12/4/19 Today


        
    }

    // helper method to perform search operation
    private void performSearch(String query){
        // Todo: search operation

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
    }

    private void clearButtonBehaviour() {
    }

    private void sortSearchResult() {

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

            // Todo : complete this method later

        }
    };


    private OnSuggestionChangeListener mOnSuggestionChangeListener = new OnSuggestionChangeListener() {
        @Override
        public void onSuggestionPicked(SuggestionItem suggestion) {
            // Handle a suggestion pick event. This is the place where you'd
            // want to perform a search against your data provider.
            String query = suggestion.getItemModel().getText();

            performSearch(query);
        }

        @Override
        public void onSuggestionRemoved(SuggestionItem suggestion) {

        }
    };





    @Override
    public void onBackPressed() {


        super.onBackPressed();

    }
}
