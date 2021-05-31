package com.chaubacho.doyourlist2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.chaubacho.doyourlist2.control.ProjectListener;
import com.chaubacho.doyourlist2.control.TaskListener;
import com.chaubacho.doyourlist2.data.values.Value;
import com.chaubacho.doyourlist2.ui.login.LoginActivity;
import com.chaubacho.doyourlist2.ui.projects.ProjectFragment;
import com.chaubacho.doyourlist2.ui.projects.TaskFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements ProjectListener, TaskListener {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validateUser();
//        mAuth.signOut();

        Log.d(TAG, "onCreate: email:" + mAuth.getCurrentUser().getEmail());

        openProjectFragment(mAuth.getCurrentUser().getEmail());
    }

    private void validateUser() {
        mAuth = FirebaseAuth.getInstance();
        // Check if login
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            Value.USER_EMAIL = mAuth.getCurrentUser().getEmail();
            Log.d(TAG, "onCreate: " + mAuth.getCurrentUser().getEmail());
        }
    }

    public void openProjectFragment(String email) {
        ProjectFragment projectFragment = new ProjectFragment(this);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.frame_container, projectFragment)
//                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openTaskFragment(String name) {
        Log.d(TAG, "openTaskFragment: opening TaskFragment = " + name);
        TaskFragment taskFragment = new TaskFragment(this, name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.frame_container, taskFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void openTaskDetailFragment(String title) {
        Log.d(TAG, "openTaskDetailFragment: opening TaskDetailFragment");
    }
}
