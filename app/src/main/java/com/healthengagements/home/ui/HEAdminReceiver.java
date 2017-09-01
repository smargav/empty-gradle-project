package com.healthengagements.home.ui;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class HEAdminReceiver extends DeviceAdminReceiver {

	public static final String ACTION = HEAdminReceiver.class.getName()
			+ "_RECEIVER";

	void showToast(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		Intent activityIntent = new Intent(context, SetupActivity.class);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(activityIntent);
		// Intent i = new Intent();
		// i.setAction(ACTION);
		// context.sendBroadcast(i);

	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return "Smargav Device Control will not work if Device Administrator is disabled.";
	}

	@Override
	public void onDisabled(Context context, Intent intent) {

	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {

	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {

	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {

	}

}
