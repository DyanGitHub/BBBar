package com.example.bbbar.application;

import android.app.Application;

public class MyApp extends Application{
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext(), "/BBBar/cash/");
	}
	private static MyApp myApp;
	public static MyApp getInstance()
	{
		if(myApp==null)
		{
			myApp=new MyApp();
		}
		return myApp;
	}	
}
