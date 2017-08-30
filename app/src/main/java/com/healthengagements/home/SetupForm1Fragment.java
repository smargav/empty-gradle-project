package com.healthengagements.home;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


public class SetupForm1Fragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.samsung_license_fragment, null);
		rootView.findViewById(R.id.enable_admin).setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		((SetupActivity) getActivity()).enableSamsungLicense();
	}

}
