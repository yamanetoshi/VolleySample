package com.example.volleysample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.volleysample.RoRDetailFragment.MyProgressDialog;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;

public class RoRListFragment extends ListFragment {
	private final String TAG = "ListFragment";
	private static final Object TAG_REQUEST_QUEUE = new Object();
	
	private RoRApplication mApp;
	private JSONArray mJsonArray;
	private String[] mArray;
	
	private final int ADD_ID = 0xdeadbeef;
	
	private final String TASKS = "tasks.json";
	
	MultiChoiceModeListener mActionModeCalback = new MultiChoiceModeListener() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			getActivity().getMenuInflater().inflate(R.menu.list_action, menu);
			mode.setTitle("対象を選択");
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.delete:
				SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
				MyAdapter adapter = (MyAdapter) getListView().getAdapter();

				requestDelete(mode, adapter, checkedItemPositions);

				return true;
			default:
				return false;
			}
		}
		
        private void requestDelete(ActionMode mode, MyAdapter adapter, 
        		SparseBooleanArray checkedItemPositions) {
        	//final ActionMode fMode = mode;
            
    		FragmentManager manager = getFragmentManager();  
            final MyProgressDialog pDialog = new MyProgressDialog();
            pDialog.show(manager, "dialog");  
        	
        	for (int i = 0; i < adapter.getCount(); i++) {
        		boolean checked = checkedItemPositions.get(i);
        		if (checked) {
        			String id = "";
        			try {
        				id = mJsonArray.getJSONObject(i).getString("id");
        			} catch (JSONException e) {
        				// TODO
        			}
        			
    	            String url = mApp.getUrl() + "tasks/" + id + ".json";
    				JsonObjectRequest request = new JsonObjectRequest(
    						Method.DELETE,
    						url,
    						null,
    						new Response.Listener<JSONObject>(){
    							@Override
    							public void onResponse(JSONObject response) {
    								//fMode.finish();
    							}
    						},
    						new Response.ErrorListener() {
    							@Override 
    							public void onErrorResponse(VolleyError error) {
    								
    								if (error instanceof com.android.volley.ParseError) {
    									//fMode.finish();
    									return;
    								}


    								//エラー時の処理
    								Toast.makeText(getActivity().getApplicationContext(), "onErrorResponse", Toast.LENGTH_LONG).show();

    							}
    						});

    				request.setTag(TAG_REQUEST_QUEUE);

    				mApp.getQueue().add(request);
    				mApp.getQueue().start(); 
        		}
        	}

        	mode.finish();
			pDialog.dismiss();
			setListAdapter(null);
			updateList();
        }

		@Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
        }
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApp = (RoRApplication)getActivity().getApplication();
        setHasOptionsMenu(true);
	}
	
	private void updateList() {
		String url = mApp.getUrl() + TASKS;
		JsonArrayRequest request = new JsonArrayRequest(
				url,
				new Response.Listener<JSONArray>(){
					@Override 
					public void onResponse(JSONArray response) {

						int length = response.length();

						mJsonArray = response;
						mArray = new String [length];
						for (int i = 0; i < length; i++) {
							try {
								JSONObject tmp = (JSONObject)response.get(i);
								mArray[i] = tmp.getString("name");
							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}
						}

						MyAdapter adapter = new MyAdapter(getActivity(), R.layout.row, mArray);
						setListAdapter(adapter);
					}
				},
				new Response.ErrorListener() {
					@Override 
					public void onErrorResponse(VolleyError error) {

						//エラー時の処理
						Toast.makeText(getActivity().getApplicationContext(), "onErrorResponse", Toast.LENGTH_LONG).show();
						Log.d(TAG, "error : " + error);
					}
				});

		request.setTag(TAG_REQUEST_QUEUE);

		mApp.getQueue().add(request);
		mApp.getQueue().start(); 
	}

	@Override
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        
		ListView lv = getListView();
		lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		lv.setMultiChoiceModeListener(mActionModeCalback);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> container, View view, int position,
					long id) {
				Fragment newFragment = new RoRDetailFragment();
				
				String ID = "";
				try {
					ID = mJsonArray.getJSONObject(position).getString("id");
				} catch (JSONException e) {
					
				}
				
				Bundle args = new Bundle();
		        args.putString("id", ID);
		        args.putString("mode", Intent.ACTION_EDIT);
		        args.putString("name", mArray[position]);
		        newFragment.setArguments(args);
		        
				FragmentTransaction transaction = getFragmentManager().beginTransaction();  
				  
				transaction.replace(R.id.container, newFragment);  
				transaction.addToBackStack(null);  
				  
				transaction.commit(); 		
			}
			
		});

		updateList();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, ADD_ID, Menu.NONE, "New Task")
			.setIcon(android.R.drawable.ic_menu_add)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case ADD_ID:
				Fragment newFragment = new RoRDetailFragment();
				
				Bundle args = new Bundle();
		        args.putString("id", "");
		        args.putString("mode", Intent.ACTION_INSERT);
		        args.putString("name", "");
		        newFragment.setArguments(args);
		        
				FragmentTransaction transaction = getFragmentManager().beginTransaction();  
				  
				transaction.replace(R.id.container, newFragment);  
				transaction.addToBackStack(null);  
				  
				transaction.commit(); 		

        		return true;
        	case android.R.id.home:
        		getActivity().finish();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}

    private class MyAdapter extends ArrayAdapter<String> {
        private String [] items;
        private LayoutInflater     inflater;
 
        public MyAdapter(Context context, int resourceId,
                String[] items) {
            super(context, resourceId, items);
            this.items = items;
            this.inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.row, null);
            }
            String item = items[position];
            TextView textView = (TextView) view
                    .findViewById(R.id.row_textview);
            textView.setText(item);
            return view;
        }
    }
}
