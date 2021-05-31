package com.chaubacho.doyourlist2.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.R;
import com.chaubacho.doyourlist2.control.ItemTaskListener;
import com.chaubacho.doyourlist2.control.TaskListener;
import com.chaubacho.doyourlist2.data.model.Task;

import java.util.List;

public class RecyclerViewTaskAdapter
        extends RecyclerView.Adapter<RecyclerViewTaskAdapter.ViewHolder> {
    private static final String TAG = "RcvTaskAdapter";
    private List<Task> taskList;
    private Context context;
    private TaskListener taskListener;

    public RecyclerViewTaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.radioButton.setChecked(task.isCompleted());
        holder.name.setText(task.getName());
        holder.date.setText(task.getDate());
        holder.time.setText(task.getTime());

        holder.setClickListener(new ItemTaskListener() {
            @Override
            public void onClickListener(int position, View v) {
                Log.d(TAG, "onClickListener: clicked");
                if (taskListener instanceof MainActivity) {
                    taskListener.openTaskDetailFragment(task.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public void setTaskListener(TaskListener taskListener) {
        Log.d(TAG, "setProjectListener: set!!!");
        this.taskListener = taskListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RadioButton radioButton;
        private TextView name, date, time;
        private ItemTaskListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button_complete);
            name = itemView.findViewById(R.id.text_view_task_name);
            date = itemView.findViewById(R.id.text_view_task_date);
            time = itemView.findViewById(R.id.text_view_task_time);
            itemView.setOnClickListener(this);

        }

        public void setClickListener(ItemTaskListener listener) {
            this.clickListener = listener;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            clickListener.onClickListener(getAdapterPosition(), v);
        }
    }

}
