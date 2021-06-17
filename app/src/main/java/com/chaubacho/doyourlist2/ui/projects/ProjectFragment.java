package com.chaubacho.doyourlist2.ui.projects;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaubacho.doyourlist2.control.IUpdateFirebase;
import com.chaubacho.doyourlist2.data.model.Item;
import com.chaubacho.doyourlist2.data.model.Project;
import com.chaubacho.doyourlist2.data.values.Value;
import com.chaubacho.doyourlist2.databinding.FragmentProjectBinding;
import com.chaubacho.doyourlist2.ui.adapter.RecyclerViewProjectAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectFragment extends Fragment implements IUpdateFirebase {
    private static final String TAG = "ProjectFragment";
    private static final String USER_EMAIL = "USER EMAIL";
    private FragmentProjectBinding binding;
    private List<Project> projectList;
    private List<Project> tempList;
    private RecyclerViewProjectAdapter adapter;
//    private ProjectListener context;

    public ProjectFragment() {
        // Required empty public constructor
    }

//    public ProjectFragment(ProjectListener context) {
//        this.context = context;
//    }

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
        tempList = new ArrayList<>();

        getDataFromFirebase();

        adapter = new RecyclerViewProjectAdapter(projectList);
//        adapter.setProjectListener(context);
        binding.recyclerViewProjects.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerViewProjects.setAdapter(adapter);
    }

    @Override
    public void getDataFromFirebase() {
        Log.d(TAG, "getDataFromFirebase: called " + Value.USER_EMAIL);
        getCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: successful" + task.getResult().size());
                            projectList.clear();
                            tempList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (doc.getData().get("color") != null && doc.getData().get("name") != null) {
                                    projectList.add(new Project(doc.getId(),
                                            doc.getData().get("color").toString(),
                                            doc.getData().get("name").toString()));
                                } else {
                                    Log.e(TAG, "onComplete: data is unavailable");
                                }
                            }
//                            Toast.makeText(getContext(), "Fetched all data", Toast.LENGTH_SHORT).show();
                            tempList.addAll(projectList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "onComplete: " + task.getException());
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        Log.d(TAG, "getDataFromFirebase: end");
    }

    @Override
    public void addDataToFirebase(Item item) {
        Project newProject = (Project) item;
        if (checkIfNameIsValid(newProject.getName(), Value.ADD_ITEM) == Value.PROJECT_NAME_INVALID) {
            Toast.makeText(getContext(), "Project name must be unique!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "addDataToFirebase: Project name must be unique!");
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("color", newProject.getColor());
            data.put("name", newProject.getName());
            getCollection()
                    .add(data)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: add project successful");
                                Toast.makeText(getContext(), "Added!", Toast.LENGTH_SHORT).show();
                                projectList.add(newProject);
                                tempList.add(newProject);
                                adapter.notifyDataSetChanged();
//                                getDataFromFirebase();

                            } else {
                                Log.e(TAG, "onComplete: Something wrong" + task.getException());
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void updateDataToFirebase(Item item) {
        Project project = (Project) item;
        if (checkIfNameIsValid(project.getName(), Value.UPDATE_ITEM) == Value.PROJECT_NAME_INVALID) {
            Toast.makeText(getContext(), "Project name must be unique!", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("color", project.getColor());
            data.put("name", project.getName());
            getCollection()
                    .document(project.getId())
                    .update(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
//                                getDataFromFirebase();
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "onComplete: Something wrong" + task.getException());
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public int checkIfNameIsValid(String name, int code) {
        int result = 0;
        for (int i = 0; i < projectList.size(); i++) {
            if (projectList.get(i).getName().equals(name)) ++result;
        }
        if (code == Value.ADD_ITEM) {
            return (result == 0) ? Value.PROJECT_NAME_VALID : Value.PROJECT_NAME_INVALID;
        } else if (code == Value.UPDATE_ITEM) {
            return (result <= 1) ? Value.PROJECT_NAME_VALID : Value.PROJECT_NAME_INVALID;
        }
        return Value.PROJECT_NAME_INVALID;
    }

    public void refresh() {
        getDataFromFirebase();
    }

    @Override
    public void deleteDataInFirebase(Item item) {
        Log.d(TAG, "deleteDataInFirebase: called");
        Project project = (Project) item;
        getCollection()
                .document(project.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                        projectList.remove(project);
                        tempList.remove(project);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "onFailure: " + e);
                    }
                });
    }

    private CollectionReference getCollection() {
        return FirebaseFirestore
                .getInstance()
                .collection("user")
                .document(Value.USER_EMAIL)
                .collection("project");
    }

    public void findByName(String name) {
        Log.d(TAG, "findByName: name = " + name);
        projectList.clear();
        if (name.isEmpty()) {
            projectList.addAll(tempList);
            return;
        }
        for (Project p : tempList) {
            if (p.getName().contains(name))
                projectList.add(p);
        }
        Log.d(TAG, "findByName: size= " + projectList.size());
        adapter.notifyDataSetChanged();
    }
}
