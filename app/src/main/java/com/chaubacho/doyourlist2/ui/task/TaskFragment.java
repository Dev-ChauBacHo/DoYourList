package com.chaubacho.doyourlist2.ui.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.control.IUpdateFirebase;
import com.chaubacho.doyourlist2.control.TaskListener;
import com.chaubacho.doyourlist2.data.model.Item;
import com.chaubacho.doyourlist2.data.model.Task;
import com.chaubacho.doyourlist2.data.values.Value;
import com.chaubacho.doyourlist2.databinding.FragmentTaskBinding;
import com.chaubacho.doyourlist2.ui.adapter.RecyclerViewTaskAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskFragment extends Fragment implements IUpdateFirebase {
    private static final String TAG = "TaskFragment";
    private FragmentTaskBinding binding;
    private List<Task> taskList;
    private List<Task> tempList;
    private RecyclerViewTaskAdapter adapter;
    private TaskListener context;
    private String projectID;
    private int CURRENT_TASK_STATUS;

    public TaskFragment() {
        // Required empty public constructor
    }

    public TaskFragment(TaskListener context, String projectID, int CURRENT_TASK_STATUS) {
        this.context = context;
        this.projectID = projectID;
        this.CURRENT_TASK_STATUS = CURRENT_TASK_STATUS;
    }

    public TaskFragment(TaskListener context, String projectID) {
        this.context = context;
        this.projectID = projectID;
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
        tempList = new ArrayList<>();

        adapter = new RecyclerViewTaskAdapter(taskList);

//        getDataFromFirebase();
        showTaskOptions(CURRENT_TASK_STATUS);

        adapter.setTaskListener(context);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerViewTasks.setAdapter(adapter);
    }

    @Override
    public void getDataFromFirebase() {
        if (taskList == null) taskList = new ArrayList<>();
        Log.d(TAG, "getDataFromFirebase: called " + projectID);
        CollectionReference collection = getCollection();
        collection
                .orderBy("isCompleted", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: successful get Data: " + task.getResult().size());
                            taskList.clear();
                            tempList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Task newTask = new Task();
                                Map<String, Object> data = doc.getData();
                                newTask.setId(doc.getId());
                                if (data.get("name") != null)
                                    newTask.setName(data.get("name").toString());
                                if (data.get("isCompleted") != null)
                                    newTask.setCompleted((Boolean) data.get("isCompleted"));
                                if (data.get("time") != null)
                                    newTask.setTime(data.get("time").toString());
                                if (data.get("date") != null)
                                    newTask.setDate(data.get("date").toString());
                                taskList.add(newTask);
                            }
                            if (CURRENT_TASK_STATUS == Value.HIDE_COMPLETED_TASK) {
                                Log.d(TAG, "showTaskOptions: Hide. TaskList size = " + taskList.size());
                                taskList.removeIf(Task::isCompleted);
                            }
                            tempList.addAll(taskList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "onComplete: " + task.getException());
                        }
                    }
                });

    }

    @Override
    public void addDataToFirebase(Item item) {
        Task task = (Task) item;
        Log.d(TAG, "addDataToFirebase: called");
        CollectionReference collection = getCollection();

        Map<String, Object> data = buildData(task);

        collection.add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: add task successful");
                            Toast.makeText(((MainActivity) context).getBaseContext(), "Added!", Toast.LENGTH_SHORT).show();
//                            taskList.add(newProject);
//                            ((MainActivity)context).getSupportFragmentManager().popBackStack();
//                            getDataFromFirebase();

                        } else {
                            Log.e(TAG, "onComplete: Something wrong" + task.getException());
                            Toast.makeText(((MainActivity) context).getBaseContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void updateDataToFirebase(Item item) {
        Task task = (Task) item;
        Log.d(TAG, "updateDataToFirebase: called");
        CollectionReference collection = getCollection();

        Map<String, Object> data = buildData(task);

        collection
                .document(task.getId())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(((MainActivity) context).getBaseContext(), "Updated!", Toast.LENGTH_SHORT).show();
                        FragmentManager manager = ((MainActivity) context).getSupportFragmentManager();
                        if (manager.findFragmentByTag("task") != null && manager.findFragmentByTag("task").isVisible()) {

                        } else {
                            manager.popBackStack();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: cannot update task" + e);
                        Toast.makeText(((MainActivity) context).getBaseContext(),
                                "Cannot update task!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @Override
    public void deleteDataInFirebase(Item item) {
        Task task = (Task) item;
        Log.d(TAG, "deleteDataInFirebase: called with task's name = " + task.getName());
        getCollection().document(task.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(((MainActivity) context).getBaseContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                        ((MainActivity) context).getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: cannot update task" + e);
                        Toast.makeText(((MainActivity) context).getBaseContext(),
                                "Cannot update task!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });


    }

//    public void refresh() {
//        getDataFromFirebase();
//    }

    private CollectionReference getCollection() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collection = firestore.collection("user")
                .document(Value.USER_EMAIL)
                .collection("project")
                .document(projectID)
                .collection("task");
        return collection;
    }

    private Map<String, Object> buildData(Task task) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", task.getName());
        data.put("isCompleted", task.isCompleted());
        if (task.getDate() != null)
            data.put("date", task.getDate());
        if (task.getTime() != null)
            data.put("time", task.getTime());
        return data;
    }

    public void showTaskOptions(int code) {
        CURRENT_TASK_STATUS = code;
        Log.d(TAG, "showTaskOptions: Called with code = " + code);
        getDataFromFirebase();
    }

    public void findByName(String name) {
        Log.d(TAG, "findByName: name = " + name);
        taskList.clear();
        if (name.isEmpty() || name.length() == 0 || name == null) {
            taskList.addAll(tempList);
            return;
        }
        for (Task t: tempList) {
            if (t.getName().contains(name))
                taskList.add(t);
        }
        Log.d(TAG, "findByName: size= " + taskList.size());
//        adapter = new RecyclerViewProjectAdapter(projectList);
        adapter.notifyDataSetChanged();
    }
}