package com.healthengagements.home.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthengagements.home.R;

public class SetupForm0Fragment extends Fragment implements View.OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.device_admin_fragment, null);
		rootView.findViewById(R.id.enable_admin).setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		((SetupActivity) getActivity()).enableAdmin();
	}

}
