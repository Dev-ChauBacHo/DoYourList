package com.chaubacho.doyourlist2.ui.projects;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.control.IUpdateItem;
import com.chaubacho.doyourlist2.data.model.Project;
import com.chaubacho.doyourlist2.databinding.FragmentAddUpdateProjectBinding;

public class AddUpdateProjectFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "AddUpdateProject";
    private FragmentAddUpdateProjectBinding binding;
    private Project project;
    private IUpdateItem context;

    public AddUpdateProjectFragment() {
        // Required empty public constructor
    }

    public AddUpdateProjectFragment(IUpdateItem context, Project project) {
        this.project = project;
        this.context = context;
    }

    public AddUpdateProjectFragment(IUpdateItem context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddUpdateProjectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (project != null) {
            binding.editTextProjectName.setText(project.getName());
            // TODO get color
            binding.buttonUpdateProject.setEnabled(true);
            binding.buttonDeleteProject.setEnabled(true);
            binding.buttonDeleteProject.setBackgroundColor(Color.RED);
        } else {
            binding.buttonAddProject.setEnabled(true);
        }
        binding.buttonAddProject.setOnClickListener(this);
        binding.buttonUpdateProject.setOnClickListener(this);
        binding.buttonDeleteProject.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO set color for project
        String name = binding.editTextProjectName.getText().toString();
        String color = "#FFFFFF";
        if (name.length() == 0) {
            Toast.makeText((MainActivity) context, "Name cannot empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v == binding.buttonAddProject) {
            project = new Project(color, name);
            context.addItem(project);
        } else if (v == binding.buttonUpdateProject) {
            if (project.getName().equals(name)) {
                Toast.makeText(getContext(), "Name cannot the same", Toast.LENGTH_SHORT).show();
            } else {
                project.setName(name);
                project.setColor(color);
                context.updateItem(project);
            }
        } else if (v == binding.buttonDeleteProject) {
            context.deleteItem(project);
        }
    }

}