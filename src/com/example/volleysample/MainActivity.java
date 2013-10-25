package com.example.volleysample;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final String TAG_LOG = "MainActivity";
	private RequestQueue mRequestQueue;
	private static final Object TAG_REQUEST_QUEUE = new Object();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		if(null == savedInstanceState){
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.container, new RoRListFragment());  
			transaction.commit();
		}

/*
		String url = "http://fathomless-temple-1877.herokuapp.com/tasks.json";
		mRequestQueue = Volley.newRequestQueue(this);
		JsonArrayRequest request = new JsonArrayRequest(
				url,
				new Response.Listener<JSONArray>(){
					@Override 
					public void onResponse(JSONArray response) {

						int length = response.length();

						//レスポンス受け取り時の処理
						if (length <= 0){
							//空の場合
							Toast.makeText(getApplicationContext(), "response is null", Toast.LENGTH_SHORT).show();
							return;
						}

						for(int i = 0; i < length; i++){
							try {
								Integer value = (Integer)response.get(i);
								Log.d(TAG_LOG, value.toString());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						//
						Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

					}
				},
				new Response.ErrorListener() {
					@Override 
					public void onErrorResponse(VolleyError error) {

						//エラー時の処理
						Toast.makeText(getApplicationContext(), "onErrorResponse", Toast.LENGTH_LONG).show();

					}
				}
				);
		//タグを設定する
		request.setTag(TAG_REQUEST_QUEUE);

		//リクエスト＆レスポンス情報の設定を追加
		mRequestQueue.add(request);

		//リクエスト開始
		mRequestQueue.start(); 
*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
