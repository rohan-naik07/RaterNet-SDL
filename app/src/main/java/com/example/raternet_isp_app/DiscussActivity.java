package com.example.raternet_isp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.raternet_isp_app.adapter.DiscussAdapter;
import com.example.raternet_isp_app.models.Constants;
import com.example.raternet_isp_app.models.Discussion;
import com.example.raternet_isp_app.models.ReviewDetails;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class DiscussActivity extends AppCompatActivity implements View.OnClickListener{

    public RecyclerView discussRecView;
    public DiscussAdapter adapter;

    public TextView isp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);

        isp=findViewById(R.id.btnNewDiscussion);
        isp.setText(Constants.ISP_Name);

        discussRecView=findViewById(R.id.discussRecView);
        discussRecView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Discussion> options =
                new FirebaseRecyclerOptions.Builder<Discussion>()
                        .setQuery(FirebaseDatabase.getInstance().getReference()
                                .child("Discussions").orderByChild("isp").equalTo(Constants.ISP_Name), Discussion.class)
                        .build();

        adapter=new DiscussAdapter(options);

        discussRecView.setAdapter(adapter);
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnNewDiscussion:
                startActivity(new Intent(DiscussActivity.this,CreateDiscussionActivity.class));
                this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed ()
    {
        startActivity(new Intent(DiscussActivity.this,MainActivity.class));
    }
}