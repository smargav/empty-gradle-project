package com.healthengagements.home.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.DeviceInventory;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.LocationPolicy;
import android.app.enterprise.MiscPolicy;
import android.app.enterprise.RestrictionPolicy;
import android.app.enterprise.RoamingPolicy;
import android.app.enterprise.SecurityPolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.healthengagements.home.R;

import java.io.File;

public class PhoneControlActivity extends Activity implements View.OnClickListener {

	private ComponentName mDeviceAdmin;
	private EnterpriseDeviceManager mEDM;
	private DevicePolicyManager mDPM;
	MiscPolicy miscPolicy;
	SecurityPolicy securityPolicy;
	RestrictionPolicy restrictionPolicy;
	RoamingPolicy roamingPolicy;
	LocationPolicy locationPolicy;
	ApplicationPolicy appPolicy;
	DeviceInventory deviceInventory;

	Button admin;

	public static final String TAG = PhoneControlActivity.class.getSimpleName();
	static final int RESULT_ENABLE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDeviceAdmin = new ComponentName(this, HEAdminReceiver.class);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		try {
			mEDM = new EnterpriseDeviceManager(this);
			miscPolicy = mEDM.getMiscPolicy();
			securityPolicy = mEDM.getSecurityPolicy();
			restrictionPolicy = mEDM.getRestrictionPolicy();
			roamingPolicy = mEDM.getRoamingPolicy();
			locationPolicy = mEDM.getLocationPolicy();
			appPolicy = mEDM.getApplicationPolicy();
			deviceInventory = mEDM.getDeviceInventory();

			Toast.makeText(
					this,
					"Device Supports Samsung Enterprise SDK v"
							+ mEDM.getEnterpriseSdkVer(), Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			Toast.makeText(
					this,
					"Device does not Supports Samsung Enterprise SDK - "
							+ e.getLocalizedMessage(), Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}
		final boolean adminActive = mDPM.isAdminActive(mDeviceAdmin);
		if (!adminActive) {
			setContentView(R.layout.activity_poc);
			findViewById(R.id.admin).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					enableDeviceAdmin();
				}
			});

		} else {
			setContentView(R.layout.features);
			initListeners();
		}
	}

	public void enableDeviceAdmin() {

		try {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					mDeviceAdmin);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"Smargav MDM For Home.");
			startActivityForResult(intent, RESULT_ENABLE);
			finish();

		} catch (Exception e) {
			Log.w(TAG, "Exception" + e);
			Toast.makeText(PhoneControlActivity.this, "Exception", Toast.LENGTH_LONG)
					.show();
		}

	}

	private void initListeners() {
		int[] ids = { R.id.eapp, R.id.ebt, R.id.ecamera, R.id.efactoryReset,
				R.id.egps, R.id.epoweroff, R.id.esdcard, R.id.esettings,
				R.id.estartApp, R.id.estore, R.id.evoicedialer,
				R.id.ewhitelist, R.id.ewifi, R.id.dapp, R.id.dbt, R.id.dcamera,
				R.id.dfactoryReset, R.id.dgps, R.id.dpoweroff, R.id.dsdcard,
				R.id.dsettings, R.id.dStopApp, R.id.dstore, R.id.dvoicedialer,
				R.id.dwhitelist, R.id.dwifi, R.id.statistics };

		for (int i : ids) {
			findViewById(i).setOnClickListener(this);
		}

	}

	@Override
	public void onClick(View v) {

		try {
			switch (v.getId()) {
			case R.id.eapp:
				installApp();
				break;
			case R.id.dapp:
				uninstallApp();
				break;
			case R.id.ebt:
				showAppropriateToast("Bluetooth Enabled",
						restrictionPolicy.allowBluetooth(true));
				break;
			case R.id.dbt:
				showAppropriateToast("Bluetooth Disaled",
						restrictionPolicy.allowBluetooth(false));
				break;
			case R.id.ecamera:
				showAppropriateToast("Camera Enabled",
						restrictionPolicy.setCameraState(true));
				break;
			case R.id.dcamera:
				showAppropriateToast("Camera Disabled",
						restrictionPolicy.setCameraState(false));
				break;
			case R.id.efactoryReset:
				showAppropriateToast("FactoryReset Enabled",
						restrictionPolicy.allowFactoryReset(true));
				break;
			case R.id.dfactoryReset:
				showAppropriateToast("FactoryReset Disabled",
						restrictionPolicy.allowFactoryReset(false));
				break;
			case R.id.epoweroff:
				showAppropriateToast("PowerOff Enabled",
						restrictionPolicy.allowPowerOff(true));
				break;
			case R.id.dpoweroff:
				showAppropriateToast("PowerOff Disabled",
						restrictionPolicy.allowPowerOff(false));
				break;
			case R.id.esdcard:
				showAppropriateToast("SD Card Write Enabled",
						restrictionPolicy.allowSDCardWrite(true));
				break;
			case R.id.dsdcard:
				showAppropriateToast("SDCard Write Disabled",
						restrictionPolicy.allowSDCardWrite(false));
				break;
			case R.id.esettings:
				showAppropriateToast("Settings Enabled",
						restrictionPolicy.allowSettingsChanges(true));
				break;
			case R.id.dsettings:
				showAppropriateToast("Settings Disabled",
						restrictionPolicy.allowSettingsChanges(false));
				break;
			case R.id.estartApp:
				showAppropriateToast(
						"Contacts App Started",
						appPolicy
								.startApp("com.android.contacts",
										"com.android.contacts.DialtactsContactsEntryActivity"));
				break;
			case R.id.dStopApp:
				showAppropriateToast("Contacts App Stopped",
						appPolicy.stopApp("com.android.contacts"));
				break;
			case R.id.estore:
				appPolicy.enableAndroidMarket();
				showAppropriateToast("Play Store Enabled", true);
				break;
			case R.id.dstore:
				appPolicy.disableAndroidMarket();
				showAppropriateToast("Play Store Disabled", true);
				break;
			case R.id.evoicedialer:
				appPolicy.enableVoiceDialer();
				showAppropriateToast("Voice Dialer Enabled", true);
				break;
			case R.id.dvoicedialer:
				appPolicy.disableVoiceDialer();
				showAppropriateToast("Voice Dialer Disabled", true);
				break;
			case R.id.ewhitelist:
				changeAppStatus(false);
				break;
			case R.id.dwhitelist:
				changeAppStatus(true);

				break;
			case R.id.egps:
				showAppropriateToast("Users can change GPS State",
						locationPolicy.setGPSStateChangeAllowed(true));
				break;
			case R.id.dgps:
				showAppropriateToast("GPS State cannot be changed.",
						locationPolicy.setGPSStateChangeAllowed(false));
				break;
			case R.id.statistics:
				showStatistics();
				break;

			}

		} catch (NoSuchMethodError e) {

			Toast.makeText(
					this,
					"Looks like this Policy is not supported on this Phone. Error:"
							+ e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (RuntimeException e) {
			Toast.makeText(this, "Runtime Exception:" + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}

	private void showStatistics() {

		StringBuffer info = new StringBuffer();
		info.append("CPU usage of App : "
				+ appPolicy.getApplicationCpuUsage(getPackageName()));
		info.append("\n");
		info.append("RAM Usage of App : "
				+ (appPolicy.getApplicationMemoryUsage(getPackageName()) / (1024 * 1024)));
		info.append("\n");
		info.append("Available RAM : "
				+ (deviceInventory.getAvailableRamMemory() / (1024 * 1024)));
		info.append("\n");
		info.append("Cache size of App : "
				+ appPolicy.getApplicationCacheSize(getPackageName()));
		info.append("\n");
		info.append("Data Size of App  : "
				+ appPolicy.getApplicationDataSize(getPackageName()));
		info.append("\n");
		info.append("Is Roaming Data Enabled : "
				+ roamingPolicy.isRoamingDataEnabled());
		info.append("\n");
		info.append("Total Data Received Network: "
				+ (deviceInventory.getBytesReceivedNetwork() / (1024 * 1024)));
		info.append("\n");
		info.append("Total Data Sent Network: "
				+ (deviceInventory.getBytesSentNetwork() / (1024 * 1024)));
		info.append("\n");
		info.append("Total Data Received WiFi: "
				+ (deviceInventory.getBytesReceivedWiFi() / (1024 * 1024)));
		info.append("\n");
		info.append("Total Data Sent WiFi: "
				+ (deviceInventory.getBytesSentWiFi() / (1024 * 1024)));
		info.append("\n");
		info.append("Is Device Protected: " + deviceInventory.isDeviceSecure());
		info.append("\n");

		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(info).setCancelable(true)
				.setPositiveButton("OK", null).create().show();
	}

	private void showAppropriateToast(String message, boolean result) {
		if (result) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this,
					"Error occured while changing state - " + message,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void installApp() {
		final EditText editText = new EditText(this);
		new Builder(this).setTitle("Enter Full Path of APK").setView(editText)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String path = editText.getText().toString();
						File fPath = new File(path);
						if (!fPath.exists()) {
							Toast.makeText(PhoneControlActivity.this,
									"Path Does not exist", Toast.LENGTH_SHORT)
									.show();
							return;
						}
						appPolicy.installApplication(fPath.getAbsolutePath(),
								false);
					}
				}).create().show();

	}

	private void uninstallApp() {
		final EditText editText = new EditText(this);
		new Builder(this).setTitle("Enter Package Name").setView(editText)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String pkg = editText.getText().toString();

						if (appPolicy.getApplicationName(pkg) == null) {
							Toast.makeText(PhoneControlActivity.this,
									"Package name is invalid.",
									Toast.LENGTH_SHORT).show();
						} else
							appPolicy.uninstallApplication(pkg, false);
					}
				}).create().show();

	}

	private void changeAppStatus(final boolean isBlacklisting) {
		final EditText editText = new EditText(this);
		new Builder(this).setTitle("Enter Package Name").setView(editText)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String pkg = editText.getText().toString();

						if (isBlacklisting) {
							showAppropriateToast("App " + pkg
									+ " is Blacklisted",
									appPolicy.addAppPackageNameToBlackList(pkg));
						} else {
							showAppropriateToast("App " + pkg
									+ " is Whitelisted",
									appPolicy.addAppPackageNameToWhiteList(pkg));
						}
					}

				}).create().show();

	}
}
