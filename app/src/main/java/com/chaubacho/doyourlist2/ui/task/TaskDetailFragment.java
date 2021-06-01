package com.chaubacho.doyourlist2.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaubacho.doyourlist2.R;
import com.chaubacho.doyourlist2.control.IUpdateItem;
import com.chaubacho.doyourlist2.data.model.Task;
import com.chaubacho.doyourlist2.databinding.FragmentTaskDetailBinding;

import java.util.Calendar;

public class TaskDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TaskDetailFragment";
    private FragmentTaskDetailBinding binding;
    private Task task;
    private IUpdateItem context;

    public TaskDetailFragment() {
        // Required empty public constructor
    }

    public TaskDetailFragment(IUpdateItem context) {
        this.context = context;
    }

    public TaskDetailFragment(IUpdateItem context, Task task) {
        this.task = task;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (task != null) {
            binding.editTextTaskName.setText(task.getName());
            if (task.getDate().length() > 0)
                binding.buttonTaskChooseDate.setText(task.getDate());
            if (task.getTime().length() > 0)
            binding.buttonTaskChooseTime.setText(task.getTime());

            binding.buttonUpdateTask.setEnabled(true);
            binding.buttonDeleteTask.setEnabled(true);
            binding.buttonDeleteTask.setBackgroundColor(Color.RED);
        } else {
            binding.checkBoxTaskDetailComplete.setEnabled(false);
            binding.buttonAddTask.setEnabled(true);
        }

        binding.buttonTaskChooseDate.setOnClickListener(this);
        binding.buttonDeleteDate.setOnClickListener(this);
        binding.buttonTaskChooseTime.setOnClickListener(this);
        binding.buttonDeleteTime.setOnClickListener(this);
        binding.buttonUpdateTask.setOnClickListener(this);
        binding.buttonAddTask.setOnClickListener(this);
        binding.buttonDeleteTask.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = binding.editTextTaskName.getText().toString();
        String date = binding.buttonTaskChooseDate.getText().toString();
        String time = binding.buttonTaskChooseTime.getText().toString();
        if (v == binding.buttonTaskChooseDate) {
            chooseDate();
        } else if (v == binding.buttonTaskChooseTime) {
            chooseTime();
        } else if (v == binding.buttonDeleteDate) {
            binding.buttonTaskChooseDate.setText(getString(R.string.choose_date));
        } else if (v == binding.buttonDeleteTime) {
            binding.buttonTaskChooseTime.setText(getString(R.string.choose_time));
        } else if (v == binding.buttonAddTask) {
            task = new Task();
            if (name.length() == 0) {
                Toast.makeText(getContext(), "Name cannot by empty", Toast.LENGTH_SHORT).show();
                return;
            } else {
                task.setName(name);
            }
            if (!date.equals(getString(R.string.choose_date)) && date.length() > 0)
                task.setDate(date);
            if (!time.equals(getString(R.string.choose_time)) && date.length() > 0) {
                task.setTime(time);
                // Don't set the date
                if (date.equals(getString(R.string.choose_date))) {
                    task.setDate(buildTodayDate());
                }
            }
            context.addItem(task);
        } else if (v == binding.buttonUpdateTask) {
            if (!date.equals(getString(R.string.choose_date)) && date.length() > 0)
                task.setDate(date);
            else
                task.setDate("");

            if (!time.equals(getString(R.string.choose_time)) && date.length() > 0) {
                task.setTime(time);
                // Don't set the date
                if (date.equals(getString(R.string.choose_date))) {
                    task.setDate(buildTodayDate());
                }
            }
            else
                task.setTime("");

            task.setCompleted(binding.checkBoxTaskDetailComplete.isChecked());
            task.setName(name);
            context.updateItem(task);
        } else if (v == binding.buttonDeleteTask) {
            context.deleteItem(task);
        }
    }

    private void chooseTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                binding.buttonTaskChooseTime.setText(hourOfDay + ":" + minute);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void chooseDate() {
        Calendar calendar = Calendar.getInstance();
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.buttonTaskChooseDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private String buildTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        return (mDay + "/" + (mMonth + 1) + "/" + mYear);
    }
}