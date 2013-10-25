package com.example.volleysample;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;

public class RoRApplication extends Application {
	private RequestQueue mRequestQueue;
	protected RequestQueue getQueue() { return mRequestQueue; }
	
	private final String URL = "http://fathomless-temple-1877.herokuapp.com/";
	protected String getUrl() { return URL; }
	
	@Override
	public void onCreate() {
		super.onCreate();
		mRequestQueue = Volley.newRequestQueue(this);		
	}
}
