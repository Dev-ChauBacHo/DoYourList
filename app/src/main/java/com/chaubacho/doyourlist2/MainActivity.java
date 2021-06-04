package com.chaubacho.doyourlist2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.chaubacho.doyourlist2.control.IUpdateItem;
import com.chaubacho.doyourlist2.control.ProjectListener;
import com.chaubacho.doyourlist2.control.TaskListener;
import com.chaubacho.doyourlist2.data.model.Item;
import com.chaubacho.doyourlist2.data.model.Project;
import com.chaubacho.doyourlist2.data.model.Task;
import com.chaubacho.doyourlist2.data.values.Value;
import com.chaubacho.doyourlist2.ui.login.LoginActivity;
import com.chaubacho.doyourlist2.ui.projects.AddUpdateProjectFragment;
import com.chaubacho.doyourlist2.ui.projects.ProjectFragment;
import com.chaubacho.doyourlist2.ui.task.TaskDetailFragment;
import com.chaubacho.doyourlist2.ui.task.TaskFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        ProjectListener,
        TaskListener,
        IUpdateItem {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private Menu mainMenu;
    private int CURRENT_TASK_STATUS = Value.SHOW_COMPLETED_TASK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validateUser();

        openProjectFragment();
        findViewById(R.id.fab_add).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mainMenu = menu;
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String name) {
                Log.d(TAG, "onQueryTextChange: Searching...");
                ProjectFragment fragment = findProjectFragment();
                if (fragment != null) {
                    fragment.findByName(name);
                }
                TaskFragment taskFragment = findTaskFragment();
                if (taskFragment != null) {
                    taskFragment.findByName(name);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            case R.id.menu_sync:
                ProjectFragment fragment = findProjectFragment();
                if (fragment != null) {
                    fragment.refresh();
                }
                TaskFragment taskFragment = findTaskFragment();
                if (taskFragment != null) {
                    taskFragment.showTaskOptions(CURRENT_TASK_STATUS);
                }
                return true;
            case R.id.menu_show_completed_task:
                TaskFragment taskFragment1 = findTaskFragment();
                if (taskFragment1 != null) {
                    if (CURRENT_TASK_STATUS == Value.HIDE_COMPLETED_TASK) {
                        Log.d(TAG, "onOptionsItemSelected: Options Show task");
                        CURRENT_TASK_STATUS = Value.SHOW_COMPLETED_TASK;
                    } else {
                        Log.d(TAG, "onOptionsItemSelected: Options Hide task");
                        CURRENT_TASK_STATUS = Value.HIDE_COMPLETED_TASK;
                    }
                    taskFragment1.showTaskOptions(CURRENT_TASK_STATUS);
                    return true;
                }
                return false;

        }
        return super.onOptionsItemSelected(item);
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

    public void openProjectFragment() {
        ProjectFragment projectFragment = new ProjectFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.frame_container, projectFragment, "project")
//                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openTaskFragment(String id) {
        Value.PROJECT_ID = id;
        Log.d(TAG, "openTaskFragment: opening TaskFragment = " + id);
        TaskFragment taskFragment = new TaskFragment(this, id, CURRENT_TASK_STATUS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.frame_container, taskFragment, "task")
                .addToBackStack("task")
                .commit();
        mainMenu.findItem(R.id.menu_show_completed_task).setVisible(true);
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack();
        if (findTaskFragment() != null) {
            mainMenu.findItem(R.id.menu_show_completed_task).setVisible(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void openTaskDetailFragment(Task task) {
        Log.d(TAG, "openTaskDetailFragment: opening TaskDetailFragment " + task.getId());
        TaskDetailFragment taskFragment = new TaskDetailFragment(this, task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                )
                .replace(R.id.frame_container, taskFragment, "task_detail")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void updateTaskStatus(Task task) {
        TaskFragment fragment = findTaskFragment();
        if (fragment != null) {
            fragment.updateDataToFirebase(task);
            fragment.showTaskOptions(CURRENT_TASK_STATUS);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.fab_add)) {
            ProjectFragment fragment = findProjectFragment();
            if (fragment != null) {
                Log.d(TAG, "onClick: Add Project");
                new AddUpdateProjectFragment(this).show(getSupportFragmentManager(), "addProject");
            }

            TaskFragment taskFragment = findTaskFragment();
            if (taskFragment != null) {
                Log.d(TAG, "onClick: Add Task");
                TaskDetailFragment taskDetailFragment = new TaskDetailFragment(this);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        )
                        .replace(R.id.frame_container, taskDetailFragment, "task_detail")
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public void addItem(Item item) {
        if (item instanceof Project) {
            Log.d(TAG, "addItem: color = " + ((Project)item).getColor());
            ProjectFragment fragment = findProjectFragment();
            if (fragment != null) {
                fragment.addDataToFirebase(item);
            }
        } else if (item instanceof Task) {
            Log.d(TAG, "addItem: Task:" + ((Task)item).getName());
            new TaskFragment(this, Value.PROJECT_ID).addDataToFirebase(item);
        }
    }

    @Override
    public void updateItem(Item item) {
        if (item instanceof Project) {
            Log.d(TAG, "updateItem: New name: " + ((Project) item).getName());
            ProjectFragment fragment = findProjectFragment();
            if (fragment != null) {
                Log.d(TAG, "updateItem: " + ((Project) item).getId());
                fragment.updateDataToFirebase(item);
            }
        } else if (item instanceof Task) {
            Log.d(TAG, "update: Task: name = " + ((Task)item).getName());
            Log.d(TAG, "update: Task: date = " + ((Task)item).getDate());
            new TaskFragment(this, Value.PROJECT_ID).updateDataToFirebase(item);
        }
    }

    @Override
    public void deleteItem(Item item) {
        if (item instanceof Project) {
            ProjectFragment fragment = findProjectFragment();
            if (fragment != null) {
                Log.d(TAG, "deleteItem: " + ((Project) item).getId());
                fragment.deleteDataInFirebase((Project) item);
            }
        } else if (item instanceof Task) {
            Log.d(TAG, "delete: Task:" + ((Task)item).getName());
            new TaskFragment(this, Value.PROJECT_ID).deleteDataInFirebase(item);
        }
    }

    public void openUpdateProjectView(Project project) {
        ProjectFragment fragment = findProjectFragment();
        if (fragment != null) {
            Log.d(TAG, "onLongClick: Update Project");
            new AddUpdateProjectFragment(this, project).show(getSupportFragmentManager(), "updateProject");
        }

        TaskFragment fragment1 = (TaskFragment) getSupportFragmentManager().findFragmentByTag("updateTask");
        if (fragment1 != null && fragment1.isVisible()) {
            Log.d(TAG, "onLongClick: Update Task");
        }
    }

    private ProjectFragment findProjectFragment() {
        ProjectFragment fragment = (ProjectFragment) getSupportFragmentManager().findFragmentByTag("project");
        if (fragment != null && fragment.isVisible()) {
            return fragment;
        }
        return null;
    }

    private TaskFragment findTaskFragment() {
        TaskFragment fragment = (TaskFragment) getSupportFragmentManager().findFragmentByTag("task");
        if (fragment != null && fragment.isVisible()) {
            return fragment;
        }
        return null;
    }

    private TaskDetailFragment findTaskDetailFragment() {
        TaskDetailFragment fragment = (TaskDetailFragment) getSupportFragmentManager().findFragmentByTag("task_detail");
        if (fragment != null && fragment.isVisible()) {
            return fragment;
        }
        return null;
    }

}
