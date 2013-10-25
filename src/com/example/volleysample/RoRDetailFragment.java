package com.example.volleysample;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RoRDetailFragment extends Fragment {
	private final String TAG = "RoRDetailFragment";
	private static final Object TAG_REQUEST_QUEUE = new Object();

	private String mMode;
	private String mId;
	private String mName;
	
	private EditText mEditText;

	private String POST = "tasks.json";
	private String PUT = "tasks/";

	private RoRApplication mApp;
	
	public static class MyProgressDialog extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	ProgressDialog progressDialog = new ProgressDialog(getActivity());
	    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    	progressDialog.setMessage("Please wait...");

	        return progressDialog;
	    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApp = (RoRApplication)getActivity().getApplication();
        setHasOptionsMenu(true);
	}

	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_detail, container, false);
	}

	@Override
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        
	    
	    Bundle args = getArguments();
	    mMode = args.getString("mode");
	    mId = args.getString("id");
	    mName = args.getString("name");
	    
		Activity p = getActivity();
		mEditText = (EditText)p.findViewById(R.id.edittext);
		mEditText.setText(mName);
		
		Button btn = (Button)p.findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                SpannableStringBuilder sb = (SpannableStringBuilder)mEditText.getText();
                String str = sb.toString();
                
                JSONObject requestParam = null;
                try {
                	requestParam = new JSONObject("{ 'task' : { 'name' : " + str +" }}");
                } catch (JSONException e) {
                	// TODO
                }
                
        		FragmentManager manager = getFragmentManager();  
                final MyProgressDialog pDialog = new MyProgressDialog();
                pDialog.show(manager, "dialog");  
            	       
                String url = mApp.getUrl();
                int method = Method.POST;
				if (mMode.equals(Intent.ACTION_INSERT)) {
					url = url + POST;
				} else if (mMode.equals(Intent.ACTION_EDIT)) {
					url = url + PUT + mId + ".json";
					method = Method.PUT;
				}
				JsonObjectRequest request = new JsonObjectRequest(
						method,
						url,
						requestParam,
						new Response.Listener<JSONObject>(){
							@Override
							public void onResponse(JSONObject response) {
								pDialog.dismiss();
								getFragmentManager().popBackStack();
							}
						},
						new Response.ErrorListener() {
							@Override 
							public void onErrorResponse(VolleyError error) {
								
								if (error instanceof com.android.volley.ParseError) {
									pDialog.dismiss();
									getFragmentManager().popBackStack();
									return;
								}

								Log.d(TAG, "error : " + error);
								//エラー時の処理
								Toast.makeText(getActivity().getApplicationContext(), "onErrorResponse" + error, Toast.LENGTH_LONG).show();

							}
						});

				request.setTag(TAG_REQUEST_QUEUE);

				mApp.getQueue().add(request);
				mApp.getQueue().start(); 
			}
			
		});
	}
}
