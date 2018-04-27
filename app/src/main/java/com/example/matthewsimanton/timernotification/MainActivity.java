package com.example.matthewsimanton.timernotification;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class MainActivity extends Activity {
    private ProgressDialog progressDialog;
    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Counting");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);

        messageHandler = new MessageHandler();
    }

    public void startCounter(View view) {
        progressDialog.show();

        Thread thread = new Thread(new Timer());
        thread.start();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void doNotify() {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent intent = PendingIntent.getActivity(this, 100, new Intent(this, BoomActivity.class), 0);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelIdOne = "com.example.matthewsimanton.timernotification";
        CharSequence nameOne = "channel1";
        String descriptionOne = "because we have to have one";

        int importanceOne = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channelOne = new NotificationChannel(channelIdOne, nameOne, importanceOne);
        channelOne.setDescription(descriptionOne);
        channelOne.enableLights(true);
        channelOne.setLightColor(Color.BLUE);
        channelOne.enableVibration(false);
        notificationManager.createNotificationChannel(channelOne);

        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, channelIdOne);
        nb.setSmallIcon(R.mipmap.ic_launcher);
        nb.setSound(sound);
        nb.setContentTitle("Knock knock...");
        nb.setContentText("You've got a delivery.");
        nb.setContentIntent(intent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(100, nb.build());
    }

    private class Timer implements Runnable {

        @Override
        public void run() {
            for (int i = 5; i >= 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

                Bundle bundle = new Bundle();
                bundle.putInt("current count", i);

                Message message = new Message();
                message.setData(bundle);

                messageHandler.sendMessage(message);

            }

            progressDialog.dismiss();

        }
    }

    private class MessageHandler extends Handler {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message message) {
            int currentCount = message.getData().getInt("current count");
            progressDialog.setMessage("Please wait in ..." + currentCount);

            if (currentCount == 0) {
                doNotify();
            }
        }

    }
}
