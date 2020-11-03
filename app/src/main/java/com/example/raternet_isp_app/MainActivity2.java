package com.example.raternet_isp_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.raternet_isp_app.auth_preferences.SaveSharedPreferences;
import com.example.raternet_isp_app.models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener{

    private TextView btnWriteReview;
    private TextView btnSearchNetwork;
    private TextView userNameDrawer,EmailDrawer;
    private User currentUser;
    public FirebaseAuth firebaseAuth;
    private ImageView openDrawer,userPic;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private View headerView;
    private Uri imageUri;
    public static final int GALLERY_KITKAT_INTENT_CALLED = 2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        firebaseAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        openDrawer = findViewById(R.id.open_drawer);

        headerView = navigationView.getHeaderView(0);
        userNameDrawer = headerView.findViewById(R.id.nameTxt);
        EmailDrawer = headerView.findViewById(R.id.emailTxt);
        userPic = headerView.findViewById(R.id.profileImageView);
        initDrawer();
        navigationView.setNavigationItemSelectedListener(this);

        btnWriteReview = findViewById(R.id.btnWriteReview);
        btnSearchNetwork = findViewById(R.id.btnSearchNetwork);

        btnWriteReview.setOnClickListener(this);
        btnSearchNetwork.setOnClickListener(this);

        currentUser = SaveSharedPreferences.getUser(MainActivity2.this);
        String photoURI = currentUser.getPhotoURL();

        /*if(photoURI!=null){
            imageUri = Uri.parse(photoURI);
            userPic.setImageURI(imageUri);
        }*/

        userNameDrawer.setText(currentUser.getUserName());
        EmailDrawer.setText(currentUser.getEmailId());
        //Setting email in Constants.
        Constants.UserEmail=currentUser.getEmailId();
        // karan@gmail.com karan345
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        openDrawer.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnWriteReview:
                Toast.makeText(this, "Entering Isp-Ratings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity2.this, IspRatingsActivity.class));
                this.finish();
                break;

            case R.id.open_drawer:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.drawer_update:
                startActivity(new Intent(MainActivity2.this, ViewReviewActivity.class));
                this.finish();
                break;
            case R.id.drawer_logout:
                firebaseAuth.signOut();
                SaveSharedPreferences.clearUser(MainActivity2.this);
                startActivity(new Intent(MainActivity2.this, MainActivity.class));
                this.finish();
                break;
            default:
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed ()
    {
        this.finishAffinity();
    }

}





