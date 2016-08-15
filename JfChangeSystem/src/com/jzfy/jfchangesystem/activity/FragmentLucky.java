package com.jzfy.jfchangesystem.activity;

import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.R.layout;
import com.jzfy.jfchangesystem.setting.ApplicationEx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentLucky extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("USER STATUS2 fragment lucky",ApplicationEx.getApp().getUSER_STATUS()+"lalal");
		return inflater.inflate(R.layout.lucky, null);
	}

}
