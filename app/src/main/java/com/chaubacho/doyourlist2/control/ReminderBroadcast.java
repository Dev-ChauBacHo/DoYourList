package com.chaubacho.doyourlist2.control;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.R;

public class ReminderBroadcast extends BroadcastReceiver {
    private static final String TAG = "ReminderBroadcast";
    private static final String CHANNEL_ID = "DoYourList";
    private static final int NOTIFICATION_ID = 201;
    private String projectColor;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: called");
        String taskName = intent.getStringExtra("taskName");
        int color = Color.RED;
        try {
            color = Color.parseColor(projectColor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is notification channel for task");


            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.main_icon)
                .setContentTitle(taskName)
                .setContentText(taskName)
//                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                i,
                PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
