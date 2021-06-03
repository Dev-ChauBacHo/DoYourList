package com.chaubacho.doyourlist2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.R;
import com.chaubacho.doyourlist2.control.ItemClickListener;
import com.chaubacho.doyourlist2.control.TaskListener;
import com.chaubacho.doyourlist2.data.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        holder.checkBox.setChecked(task.isCompleted());
        holder.name.setText(task.getName());
        holder.date.setText(task.getDate());
        holder.time.setText(task.getTime());

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClickListener(int position, View v) {
                Log.d(TAG, "onClickListener: clicked");
                if (taskListener instanceof MainActivity) {
                    taskListener.openTaskDetailFragment(task);
                }
            }

            @Override
            public void onLongClickListener(int position, View v) {
                Log.d(TAG, "onLongClickListener: clicked");
                // TODO Handle Long click
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: RadioButton clicked");
                task.setCompleted(holder.checkBox.isChecked());
                taskListener.updateTaskStatus(task);
            }
        });

        if (task.getDate() != null && task.getDate().length() != 0) {
            int compare = compareDate(task.getDate());
            holder.date.setTextColor(compare);
            holder.time.setTextColor(compare);
        }
    }

    private int compareDate(String date) {
        String currentDateString = buildCurrentDateTime();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date remindDate = sdf.parse(date);
            Date currentDate = sdf.parse(currentDateString);
            Date diff = new Date(remindDate.getTime() - currentDate.getTime());

            if (diff.getTime() == 0) {          // Reminder is today
                return Color.BLUE;
            } else if (diff.getTime() < 0) {    // Reminder date is passed
                return Color.RED;
            } else {
                return Color.BLACK;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Color.BLACK;
    }

    private String buildCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.YEAR));
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public void setTaskListener(TaskListener taskListener) {
        Log.d(TAG, "setProjectListener: set!!!");
        this.taskListener = taskListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CheckBox checkBox;
        private TextView name, date, time;
        private ItemClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box_complete);
            name = itemView.findViewById(R.id.text_view_task_name);
            date = itemView.findViewById(R.id.text_view_task_date);
            time = itemView.findViewById(R.id.text_view_task_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener listener) {
            this.clickListener = listener;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            clickListener.onClickListener(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "onLongClick: " + getAdapterPosition());
            clickListener.onLongClickListener(getAdapterPosition(), v);
            return true;
        }
    }

}
