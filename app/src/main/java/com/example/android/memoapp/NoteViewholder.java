package com.example.android.memoapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteViewholder extends RecyclerView.ViewHolder {
    TextView singleTitle, singleTime;
    View mview;
    CardView noteCard;
    public NoteViewholder(@NonNull View itemView) {
        super(itemView);
        mview = itemView;
        singleTime = mview.findViewById(R.id.single_time);
        singleTitle = mview.findViewById(R.id.single_title);
        noteCard = mview.findViewById(R.id.note_card);

    }


    public void setNotetitle(String title) {
    singleTitle.setText(title);
    }

    public void setNotetime(String time) {

    singleTime.setText(time);
    }
}
