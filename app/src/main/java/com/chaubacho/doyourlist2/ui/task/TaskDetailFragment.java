package com.chaubacho.doyourlist2.ui.task;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.R;
import com.chaubacho.doyourlist2.control.IUpdateItem;
import com.chaubacho.doyourlist2.control.ReminderBroadcast;
import com.chaubacho.doyourlist2.data.model.Task;
import com.chaubacho.doyourlist2.data.values.Value;
import com.chaubacho.doyourlist2.databinding.FragmentTaskDetailBinding;
import com.chaubacho.doyourlist2.ui.adapter.TaskPhotoAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TaskDetailFragment";
    private FragmentTaskDetailBinding binding;
    private Task task;
    private IUpdateItem context;
    private StorageReference reference;
    private TaskPhotoAdapter adapter;
    private List<Uri> uriImage;
    private ActivityResultLauncher<String> mGetContent;

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

        reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("user")
                .child(Value.USER_EMAIL)
                .child(Value.PROJECT_ID);
        uriImage = new ArrayList<>();
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Log.d(TAG, "onActivityResult: receive");
                        if (uri != null) {
                            uriImage.add(uri);
                            binding.textViewTaskPhoto.setText("Added " + uriImage.size() + "photos");
                        }
                    }

                });

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
            binding.buttonTaskSetReminder.setEnabled(false);
            binding.buttonAddPhoto.setVisibility(View.INVISIBLE);
        }

        binding.buttonTaskChooseDate.setOnClickListener(this);
        binding.buttonDeleteDate.setOnClickListener(this);
        binding.buttonTaskChooseTime.setOnClickListener(this);
        binding.buttonDeleteTime.setOnClickListener(this);
        binding.buttonUpdateTask.setOnClickListener(this);
        binding.buttonAddTask.setOnClickListener(this);
        binding.buttonDeleteTask.setOnClickListener(this);
        binding.buttonTaskSetReminder.setOnClickListener(this);
        binding.buttonAddPhoto.setOnClickListener(this);

        adapter = new TaskPhotoAdapter();
        binding.recyclerViewTasksPhoto.setAdapter(adapter);
        binding.recyclerViewTasksPhoto.setLayoutManager(new GridLayoutManager(getContext(),
                3));

        if (task != null && task.getId() != null) {
            downloadImage();
        }

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
            } else
                task.setTime("");

            task.setCompleted(binding.checkBoxTaskDetailComplete.isChecked());
            task.setName(name);
            context.updateItem(task);
            uploadPhotoToFirebase(uriImage);
        } else if (v == binding.buttonDeleteTask) {
            context.deleteItem(task);
            deletePhotos();
        } else if (v == binding.buttonTaskSetReminder) {
            Log.d(TAG, "onClick: Set reminder");
//            createNotificationChannel();
            // date, time is not set
            if (date.equals(getString(R.string.choose_date)) && time.equals(getString(R.string.choose_time))) {
                Toast.makeText(getContext(), "Date, time can not empty", Toast.LENGTH_SHORT).show();
                return;
            }
            // set time but not set date
            if (date.equals(getString(R.string.choose_date))) date = buildTodayDate();
            // set date but not set time
            if (time.equals(getString(R.string.choose_time))) time = "0:1";
            createAlarmReminder(date, time);
            Toast.makeText(getContext(), "Set alarm successful!", Toast.LENGTH_SHORT).show();
            // TODO alarm should cancel when user add Task but not save
            // TODO Reminder should change when user update time, date
            // TODO Reminder should cancel when delete task
        } else if (v == binding.buttonAddPhoto) {
            mGetContent.launch("image/*");
        }

    }

    private void uploadPhotoToFirebase(List<Uri> uriImage) {
        for (Uri uri : uriImage) {
            reference
                    .child(task.getId())
                    .child(System.currentTimeMillis() + "." + uri.getLastPathSegment())
                    .putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: Success!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
        }
    }

    private void downloadImage() {
        Log.d(TAG, "downloadImage: starting");

        reference.child(task.getId())
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        adapter.updateData(listResult.getItems());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e);
                    }
                });
    }

    public void deletePhotos() {
        Log.d(TAG, "deletePhotoItem: ");

        reference.child(task.getId())
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference reference : listResult.getItems()) {
                            reference.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: Delete photos success!");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: Cannot delete" + e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e);
                    }
                });

        reference.child(task.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Delete photos success!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Cannot delete" + e);
                    }
                });

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

    private void createAlarmReminder(String date, String time) {
        Log.d(TAG, "createAlarmReminder: create alarm");
        // format: dd/mm/yy  hh:mm
        String[] date1 = date.split("/");
        String[] time1 = time.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Integer.parseInt(date1[2]),
                Integer.parseInt(date1[1]) - 1,
                Integer.parseInt(date1[0]),
                Integer.parseInt(time1[0]),
                Integer.parseInt(time1[1]),
                0);
        Log.d(TAG, "createAlarmReminder: " + calendar.toString());

        Intent intent = new Intent(getContext(), ReminderBroadcast.class);
        intent.putExtra("taskName", task.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) ((MainActivity) context)
                .getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent);

    }
}
