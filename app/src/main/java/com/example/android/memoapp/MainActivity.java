package com.example.android.memoapp;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseRecyclerAdapter adapter;
    Query query;
    DatabaseReference mDatabaseReference;
    private FirebaseAuth fauth;
    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.mainNotes_list);
        fauth = FirebaseAuth.getInstance();
        updateUi();
        if (fauth != null)
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notes").child(fauth.getCurrentUser().getUid());


        mRecyclerView.setHasFixedSize(true);
gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Notes").child(fauth.getCurrentUser().getUid())
                .limitToLast(50);


        FirebaseRecyclerOptions<Notemodel> options =
                new FirebaseRecyclerOptions.Builder<Notemodel>()
                        .setQuery(query, Notemodel.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Notemodel, NoteViewholder>(options) {


            @Override
            public NoteViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_note, parent, false);


                return new NoteViewholder(view);
            }

            @Override
            protected void onBindViewHolder(final NoteViewholder holder, int position, final Notemodel model) {

                final String noteId = getRef(position).getKey();
                mDatabaseReference.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("timestamp")) {
                            String title = dataSnapshot.child("title").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();
                                //holder.setNotetime(timestamp);
                                GetTimeAgo  getTimeAgo = new GetTimeAgo();
                                holder.setNotetime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp),getApplicationContext()));


                                holder.setNotetitle(title);
                            holder.noteCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, newNoteActivity.class);
                                    intent.putExtra("noteId", noteId);
                                    startActivity(intent);
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        };
        mRecyclerView.setAdapter(adapter);


    }

    public void updateUi() {

        if (fauth.getCurrentUser() != null) {
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_new_note) {
            Intent intent = new Intent(MainActivity.this, newNoteActivity.class);
            startActivity(intent);
        }

        return true;


    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private int dpTopx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));

    }
}
