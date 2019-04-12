package com.example.stackapp.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpacingItemDecorator extends RecyclerView.ItemDecoration {

    private int mVerticalSpacing;
    private int mVeritcalSpacingCompenation;

    public VerticalSpacingItemDecorator(int mVerticalSpacing, int mVeritcalSpacingCompenation) {
        this.mVerticalSpacing = mVerticalSpacing;
        this.mVeritcalSpacingCompenation = mVeritcalSpacingCompenation;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);


        if(position != parent.getAdapter().getItemCount() -1){
            outRect.bottom = mVerticalSpacing - mVeritcalSpacingCompenation;
        } else {
            outRect.bottom = mVerticalSpacing;
        }
    }
}
