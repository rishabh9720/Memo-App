package com.example.android.memoapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class newNoteActivity extends AppCompatActivity {
    Button btncreate;
    EditText etitle, econtent;
    Toolbar mToolBar;
    View view;
    private Menu menuItem;
    private boolean isExist;
    private FirebaseAuth fauth;
    private String noteId;
    private DatabaseReference notesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        mToolBar = findViewById(R.id.new_note_toolbar);
        btncreate = findViewById(R.id.new_note_button);
        etitle = findViewById(R.id.new_note_title);
        econtent = findViewById(R.id.new_note_description);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fauth = FirebaseAuth.getInstance();
        notesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fauth.getCurrentUser().getUid());

        view = findViewById(R.id.new_note_layout);
        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etitle.getText().toString().trim();
                String content = econtent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    createNote(title, content);
                } else {
                    Snackbar.make(view, "Fill Empty fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        try {
            noteId = getIntent().getStringExtra("noteId");
            if (!noteId.trim().equals("")) {

                isExist = true;
            } else
                isExist = false;
        } catch (Exception e) {
            e.printStackTrace();
        }


        putData();
    }

    private void putData() {
        if (isExist) {
            notesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();
                        etitle.setText(title);
                        econtent.setText(content);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void createNote(String title, String content) {
        if (fauth.getCurrentUser() != null) {

            if (isExist) {
// update an existing node
                Map updateMap = new HashMap();
                updateMap.put("title", etitle.getText().toString().trim());
                updateMap.put("content", econtent.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);
                notesDatabase.child(noteId).updateChildren(updateMap);
                Toast.makeText(this, "Updated Successfully !!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // create a new note in this

                final DatabaseReference mNotesRef = notesDatabase.push();
                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainthread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mNotesRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(newNoteActivity.this, "Note added to database", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(newNoteActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });
                mainthread.start();
            }
        } else {
            Toast.makeText(this, "User is not signed in", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_notemenu, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.new_note_delete) {
            if (isExist)
                deleteNode();
            else
                Toast.makeText(this, "Nothing to delete", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.home) {

            finish();

        }
        return true;
    }

    private void deleteNode() {
        notesDatabase.child(noteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(newNoteActivity.this, "Successfully deleted !", Toast.LENGTH_SHORT).show();
                    noteId = "no";
                    finish();
                } else {
                    Toast.makeText(newNoteActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
