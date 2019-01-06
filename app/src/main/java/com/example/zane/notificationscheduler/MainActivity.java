package com.example.zane.notificationscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

	private static final int JOB_ID = 0;
	private JobScheduler jobScheduler;

	private Switch swtCharging;
	private Switch swtIdle;
	private SeekBar seekBar;
	private TextView tvSeekProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		swtCharging = findViewById(R.id.swt_charging);
		swtIdle = findViewById(R.id.swt_idle);
		seekBar = findViewById(R.id.seekBar);
		tvSeekProgress = findViewById(R.id.tv_override_value);

		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				if (progress > 0) {
					tvSeekProgress.setText(String.valueOf(progress + " s"));
				} else {
					tvSeekProgress.setText(getString(R.string.not_set));
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	public void cancelJobs(View view) {
		if (jobScheduler != null){
			jobScheduler.cancelAll();
			jobScheduler = null;
			Toast.makeText(this, "Jobs cancelled!", Toast.LENGTH_SHORT).show();
		}
	}

	public void scheduleJob(View view) {
		RadioGroup radioGroup = findViewById(R.id.radioGroup);

		int selectedNetworkId = radioGroup.getCheckedRadioButtonId();
		int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
		int seekBarProgress = seekBar.getProgress();
		boolean seekBarSet = seekBarProgress > 0;

		switch (selectedNetworkId){
			case R.id.rad_any:
				selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
				break;
			case R.id.rad_wifi:
				selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
				break;
		}

		jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

		ComponentName componentName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
		JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName)
						.setRequiresDeviceIdle(swtIdle.isChecked())
						.setRequiresCharging(swtCharging.isChecked())
						.setRequiredNetworkType(selectedNetworkOption);

		if (seekBarSet)
			builder.setOverrideDeadline(seekBarProgress * 1000);

		boolean constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE)
						|| swtIdle.isChecked() || swtCharging.isChecked() || seekBarSet;

		if (constraintSet){
			JobInfo jobInfo = builder.build();
			jobScheduler.schedule(jobInfo);
			Toast.makeText(this, "Job Scheduled, job will run when " +
							"the constraints are met.", Toast.LENGTH_SHORT).show();
		} else {

			Toast.makeText(this, "Please set at least one constraint",
							Toast.LENGTH_SHORT).show();
		}
	}
}
