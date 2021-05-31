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

import com.chaubacho.doyourlist2.control.ProjectListener;
import com.chaubacho.doyourlist2.data.model.Project;
import com.chaubacho.doyourlist2.data.values.Value;
import com.chaubacho.doyourlist2.databinding.FragmentProjectBinding;
import com.chaubacho.doyourlist2.ui.adapter.RecyclerViewProjectAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProjectFragment extends Fragment {
    private static final String TAG = "ProjectFragment";
    private static final String USER_EMAIL = "USER EMAIL";
    private FragmentProjectBinding binding;
    private List<Project> projectList;
    private RecyclerViewProjectAdapter adapter;
    private ProjectListener context;

    public ProjectFragment() {
        // Required empty public constructor
    }

    public ProjectFragment(ProjectListener context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProjectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        projectList = new ArrayList<>();

        getDataFromFirebase();

        adapter = new RecyclerViewProjectAdapter(projectList);
        adapter.setProjectListener(context);
        binding.recyclerViewProjects.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerViewProjects.setAdapter(adapter);
    }

    private void getDataFromFirebase() {
        Log.d(TAG, "getDataFromFirebase: called " + Value.USER_EMAIL);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user")
                .document(Value.USER_EMAIL)
                .collection("project")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: successful" + task.getResult().size());
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (doc.getData().get("color") != null) {
                                    projectList.add(new Project(doc.getData().get("color").toString(), doc.getId()));
                                } else {
                                    Log.e(TAG, "onComplete: data is unavailable");
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "onComplete: " + task.getException() );
                        }
                    }
                });
//        Log.d(TAG, "getDataFromFirebase: end");
    }
}