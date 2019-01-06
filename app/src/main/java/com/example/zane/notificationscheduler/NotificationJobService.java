package com.example.zane.notificationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationJobService extends JobService {

	private static final String PRIMARY_NOTIFICATION_CHANNEL_ID = "primary_notification_channel";
	private static final int NOTIFICATION_ID = 1;

	private NotificationManager notificationManager;


	@Override
	public boolean onStartJob(JobParameters params) {

		createNotification();

		PendingIntent pendingIntent = PendingIntent.getActivity
						(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_NOTIFICATION_CHANNEL_ID)
						.setContentTitle("Job Service")
						.setContentText("Your Job is Running")
						.setContentIntent(pendingIntent)
						.setSmallIcon(R.drawable.ic_job_running)
						.setPriority(NotificationCompat.PRIORITY_MAX)
						.setDefaults(NotificationCompat.DEFAULT_ALL)
						.setAutoCancel(true);

		notificationManager.notify(NOTIFICATION_ID, builder.build());
		return false;
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		return true;
	}

	private void createNotification(){
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel notificationChannel = new NotificationChannel(
							PRIMARY_NOTIFICATION_CHANNEL_ID,
							"Job Service Notification",
							NotificationManager.IMPORTANCE_HIGH);

			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.enableVibration(true);
			notificationChannel.setDescription("Notification Channel Description");

			notificationManager.createNotificationChannel(notificationChannel);
		}
	}
}
