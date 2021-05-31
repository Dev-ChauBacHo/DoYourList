package com.chaubacho.doyourlist2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.R;
import com.chaubacho.doyourlist2.control.ItemProjectListener;
import com.chaubacho.doyourlist2.control.ProjectListener;
import com.chaubacho.doyourlist2.data.model.Project;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class RecyclerViewProjectAdapter
        extends RecyclerView.Adapter<RecyclerViewProjectAdapter.ViewHolder> {
    private static final String TAG = "RcvProjectAdapter";
    private List<Project> projectList;
    private Context context;
    private ProjectListener projectListener;

    public RecyclerViewProjectAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.name.setText(project.getName());
        holder.color.setBackgroundColor(Color.parseColor(project.getColor()));

        holder.setClickListener(new ItemProjectListener() {
            @Override
            public void onClickListener(int position, View v) {
                Log.d(TAG, "onClickListener: clicked");
                if (projectListener instanceof MainActivity) {
                    Log.d(TAG, "onClickListener: Project's name = " + project.getName());
                    projectListener.openTaskFragment(project.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    public void setProjectListener(ProjectListener projectListener) {
        Log.d(TAG, "setProjectListener: set!!!");
        this.projectListener = projectListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ShapeableImageView color;
        private TextView name;
        private ItemProjectListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.image_project);
            name = itemView.findViewById(R.id.text_view_project_name);
            itemView.setOnClickListener(this);

        }

        public void setClickListener(ItemProjectListener listener) {
            this.clickListener = listener;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            clickListener.onClickListener(getAdapterPosition(), v);
        }
    }

}
