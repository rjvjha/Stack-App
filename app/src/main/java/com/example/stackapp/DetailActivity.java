package com.example.stackapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stackapp.adapter.AnswerAdapter;
import com.example.stackapp.databinding.ItemLayoutBinding;
import com.example.stackapp.models.Answer;
import com.example.stackapp.models.AnswerList;
import com.example.stackapp.models.Question;
import com.example.stackapp.network.RemoteCallsInterface;
import com.example.stackapp.network.RetrofitInstance;
import com.example.stackapp.utils.Converters;
import com.example.stackapp.utils.TaskHelper;
import com.example.stackapp.utils.VerticalSpacingItemDecorator;
import com.example.stackapp.utils.extension.ResourceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static List<Answer> sData = new ArrayList<>();
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private Group mEmptyView;
    private AnswerAdapter mAnswerAdapter;
    private Button mAnswerButton;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mProgressBar = findViewById(R.id.progress_bar);
        mEmptyView = findViewById(R.id.group);
        mEmptyView.setVisibility(View.GONE);

        initRecyclerView();
        initQuestionData();
        setupAnswerButton();

    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.answer_list_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(
                ResourceHelper.dpToPx(2),
                ResourceHelper.dpToPx(2)));

        if (sData == null || sData.isEmpty()) {
            mAnswerAdapter = new AnswerAdapter(new ArrayList<>());
        } else {
            mAnswerAdapter = new AnswerAdapter(sData);
        }


        mRecyclerView.setAdapter(mAnswerAdapter);


    }


    private void initQuestionData() {

        question = (Question) getIntent().getSerializableExtra("question");
        View questionView = findViewById(R.id.include);
        ItemLayoutBinding binding = DataBindingUtil.bind(questionView);
        binding.executePendingBindings();

        boolean isAnswered = Boolean.parseBoolean(question.getIs_answered());

        Drawable drawable = binding.answeredTagTv.getCompoundDrawables()[2].mutate();

        // changing answerTag
        if (!isAnswered) {
            binding.answeredTagTv.setText(R.string.answered_false);
            if (drawable != null) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.iconColor));
            }
        } else {
            binding.answeredTagTv.setText(R.string.answered_true);
            if (drawable != null) {
                DrawableCompat.setTint(drawable,
                        ContextCompat.getColor(this,
                                R.color.answered_tag_color));
            }
        }

        // set formatted title
        binding.titleTv.setText(Converters.toFormattedTitle(question.getTitle()));

        // set Creation modified date-time
        binding.creationTv.setText
                (getString(R.string.asked_label) + " " + Converters.toFormattedDateTime(question.getCreation_date()));


        // if last edit_date is null
        if (question.getLast_edit_date() != null && !question.getLast_edit_date().isEmpty()) {
            binding.lastModifiedTv.setText(
                    getString(R.string.modified_label) + " " +
                            Converters.toFormattedDateTime(question.getLast_edit_date()));
        } else {
            binding.lastModifiedTv.setVisibility(View.GONE);
        }


        // convert >1000 values in Score, answers, views
        binding.scoreTv.setText(Converters.format(Long.parseLong(question.getScore())));
        binding.answerCountTv.setText(Converters.format(Long.parseLong(question.getAnswer_count())));
        binding.viewsTv.setText(Converters.format(Long.parseLong(question.getView_count())) + " " + "views");
        binding.userNameTv.setText(question.getOwner().getDisplay_name());


        // load Profile Image from glide

        Glide
                .with(this)
                .load(question.getOwner().getProfile_image())
                .into(binding.userProfileIv);

        int id = Integer.parseInt(question.getQuestion_id());

        // start answer search
        performAnswerSearch(id);

    }

    private void performAnswerSearch(int id) {
        mRecyclerView.setAlpha(0f);
        mProgressBar.setVisibility(View.VISIBLE);

        Runnable task = () -> {
            getData(id);

        };

        new Handler().post(task);


    }

    private void getData(int id) {

        RemoteCallsInterface service = RetrofitInstance.getRetrofit()
                .create(RemoteCallsInterface.class);

        Call<AnswerList> ansList = service.getAnswers(id);
        mProgressBar.setVisibility(View.VISIBLE);
        ansList.enqueue(new Callback<AnswerList>() {
            @Override
            public void onResponse(Call<AnswerList> call, Response<AnswerList> response) {


                sData = response.body().getAnswerList();

                if (sData.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }

                mAnswerAdapter.updateItems(sData);
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.animate()
                        .alpha(1f)
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(300L)
                        .start();

            }

            @Override
            public void onFailure(Call<AnswerList> call, Throwable t) {
                showToastMessage("Something went wrong! Try again later.");
                t.printStackTrace();

            }
        });

    }

    private void setupAnswerButton() {
        mAnswerButton = findViewById(R.id.answer_button);
        mAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskHelper.openCustomChromeTab(DetailActivity.this, question.getLink());
            }
        });
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }


}
