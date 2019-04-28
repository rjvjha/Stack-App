package com.example.stackapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stackapp.R;
import com.example.stackapp.databinding.AnswerItemBinding;
import com.example.stackapp.models.Answer;
import com.example.stackapp.utils.Converters;
import com.example.stackapp.utils.TaskHelper;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private List<Answer> answerList;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public AnswerAdapter(List<Answer> answerList) {
        this.answerList = answerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        AnswerItemBinding binding = DataBindingUtil.
                inflate(layoutInflater, R.layout.answer_item, parent, false);

        return new ViewHolder(binding);
    }


    private View.OnClickListener createClickListener(String answerId) {
        return view -> {

            String url = "https://stackoverflow.com/a/" + answerId;
            TaskHelper.openCustomChromeTab(mContext, url);

        };
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Answer answer = answerList.get(position);
        holder.bind(answer, createClickListener(answer.getAnswer_id()));

    }


    @Override
    public int getItemCount() {

        if (answerList != null) {
            return answerList.size();
        }
        return 0;
    }

    // public helper method to update items
    public void updateItems(List<Answer> answers) {
        answerList.clear();
        answerList.addAll(answers);
        notifyDataSetChanged();
    }


    // public helper method to clear existing data in Adapter
    public void clear() {

        if (answerList != null) {
            answerList.clear();
            notifyDataSetChanged();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        AnswerItemBinding binding;


        public ViewHolder(@NonNull AnswerItemBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Answer answer, View.OnClickListener clickListener) {
            binding.setClickListener(clickListener);
            binding.setAnswer(answer);
            binding.acceptedTag.setVisibility(View.INVISIBLE);


            boolean isAccepted = Boolean.parseBoolean(answer.getIs_accepted());

            Drawable drawable = binding.acceptedTag.getCompoundDrawables()[2].mutate();

            // changing answerTag
            if (isAccepted) {
                binding.acceptedTag.setText(R.string.accepted);
                binding.acceptedTag.setVisibility(View.VISIBLE);
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.answered_tag_color));
                }
            }

            // set Creation modified date-time
            binding.creationDateTv.setText
                    (mContext.getString(R.string.answered_true) + " " + Converters.toFormattedDateTime(answer.getCreation_date()));


            // convert >1000 values in Score, answers, views
            binding.scoreTv.setText(Converters.format(Long.parseLong(answer.getScore())));


            // load Profile Image from glide

            Glide
                    .with(mContext)
                    .load(answer.getOwner().getProfile_image())
                    .into(binding.userProfile);

            binding.executePendingBindings();

        }
    }
}
