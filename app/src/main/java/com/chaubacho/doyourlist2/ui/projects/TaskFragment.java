package com.chaubacho.doyourlist2.ui.projects;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaubacho.doyourlist2.control.TaskListener;
import com.chaubacho.doyourlist2.data.model.Task;
import com.chaubacho.doyourlist2.data.values.Value;
import com.chaubacho.doyourlist2.databinding.FragmentTaskBinding;
import com.chaubacho.doyourlist2.ui.adapter.RecyclerViewTaskAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends Fragment {
    private static final String TAG = "TaskFragment";
    private FragmentTaskBinding binding;
    private List<Task> taskList;
    private RecyclerViewTaskAdapter adapter;
    private TaskListener context;
    private String projectName;

    public TaskFragment() {
        // Required empty public constructor
    }

    public TaskFragment(TaskListener context, String projectName) {
        this.context = context;
        this.projectName = projectName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskList = new ArrayList<>();

        adapter = new RecyclerViewTaskAdapter(taskList);

        getDataFromFirebase();

        adapter.setTaskListener(context);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerViewTasks.setAdapter(adapter);
    }

    private void getDataFromFirebase() {
        Log.d(TAG, "getDataFromFirebase: called " + projectName);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        try {
            firestore.collection("user")
                    .document(Value.USER_EMAIL)
                    .collection("project")
                    .document(projectName)
                    .collection("task")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: successful" + task.getResult().size());
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    if (doc.getData().get("name") != null) {
                                        taskList.add(new Task(doc.getData().get("name").toString()));
                                    } else {
                                        Log.e(TAG, "onComplete: data is unavailable");
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "onComplete: " + task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "getDataFromFirebase: end");
    }
}