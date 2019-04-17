package com.example.stackapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stackapp.R;
import com.example.stackapp.databinding.ItemLayoutBinding;
import com.example.stackapp.models.Question;
import com.example.stackapp.utils.Converters;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> questionsList;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public QuestionAdapter(List<Question> questionsList) {
        this.questionsList = questionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemLayoutBinding binding = DataBindingUtil.
                inflate(layoutInflater, R.layout.item_layout, parent, false);

        return new ViewHolder(binding);
    }


    private View.OnClickListener createClickListener(String linkUrl) {
        return view -> {

            // TODO: implement item click listener here
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();

        };
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Question question = questionsList.get(position);
        holder.bind(question, createClickListener(question.getLink()));

    }


    @Override
    public int getItemCount() {

        if (questionsList != null) {
            return questionsList.size();
        }
        return 0;
    }

    // public helper method to update items
    public void updateItems(List<Question> questions) {
        questionsList.clear();
        questionsList.addAll(questions);
        notifyDataSetChanged();
    }

    // public helper method to clear existing data in Adapter
    public void clear(){

        if(questionsList!=null){
            questionsList.clear();
            notifyDataSetChanged();
        }
    }




    class ViewHolder extends RecyclerView.ViewHolder {

        ItemLayoutBinding binding;


        public ViewHolder(@NonNull ItemLayoutBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Question question, View.OnClickListener clickListener) {
            binding.setClickListener(clickListener);
            binding.setQuestion(question);


            boolean isAnswered = Boolean.parseBoolean(question.getIs_answered());

            Drawable drawable = binding.answeredTagTv.getCompoundDrawables()[2].mutate();

            // changing answerTag
            if (!isAnswered) {
                binding.answeredTagTv.setText(R.string.answered_false);
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, Color.GRAY);
                }
            } else {
                binding.answeredTagTv.setText(R.string.answered_true);
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, Color.GREEN);
                }
            }

            // set formatted title
            binding.titleTv.setText(Converters.toFormattedTitle(question.getTitle()));

            // set Creation modified date-time
            binding.creationTv.setText
                    (mContext.getString(R.string.asked_label) + " " + Converters.toFormattedDateTime(question.getCreation_date()));


            // if last edit_date is null
            if (question.getLast_edit_date() != null && !question.getLast_edit_date().isEmpty()) {
                binding.lastModifiedTv.setText(
                        mContext.getString(R.string.modified_label) + " " +
                                Converters.toFormattedDateTime(question.getLast_edit_date()));
            } else{
                binding.lastModifiedTv.setVisibility(View.GONE);
            }


            // convert >1000 values in Score, answers, views
            binding.scoreTv.setText(Converters.format(Long.parseLong(question.getScore())));
            binding.answerCountTv.setText(Converters.format(Long.parseLong(question.getAnswer_count())));
            binding.viewsTv.setText(Converters.format(Long.parseLong(question.getView_count())) + " " + "views");


            // load Profile Image from glide

            Glide
                    .with(mContext)
                    .load(question.getOwner().getProfile_image())
                    .into(binding.userProfileIv);

            binding.executePendingBindings();

        }
    }
}
