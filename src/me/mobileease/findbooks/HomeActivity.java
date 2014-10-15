package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.mobileease.findbooks.adapter.OfferAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class HomeActivity extends ActionBarActivity {
	private ParseUser user;
	private GridView gridview;
	private ProgressDialog progress;
	protected List<ParseObject> userOffers;
	private OfferAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.home);
	    
	    user = ParseUser.getCurrentUser();

	    //cargar la vista grilla
	    gridview = (GridView) findViewById(R.id.gridview);
	    
	    //set clicklistener
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            	
	        		if(position == 0 || position == 1){
	        			
	        			showSearch(position);
	        			
	        		}
	        		
	        		Toast.makeText(HomeActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    if(user != null){
		    getOffers();
	    }else{
            Log.d("FB", "No hay usuario?");
	    }
	    
	    
	}

	protected void showSearch(int position) {

		Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
//	    EditText editText = (EditText) findViewById(R.id.edit_message);
//	    String message = editText.getText().toString();
//	    intent.putExtra(EXTRA_MESSAGE, message);
	    startActivity(intent);
		
	}

	/** Obtener las ofertas disponibles 
	  * (no prestados o en transaccion) del usuario
	  */
	private void getOffers() {
		
		progress = new ProgressDialog(this);
		progress.setTitle("Cargando tus libros");
		progress.setMessage("Dame un momento, estoy apunto de mostrarte tus libros...");
		progress.show();
		
        Log.d("FB", "Obteniendo Ofertas");
		
		ParseQuery<ParseObject> queryLocal = ParseQuery.getQuery("Offer");
		queryLocal.whereEqualTo("user", user);
		queryLocal.fromLocalDatastore();
		queryLocal.include("book");
		queryLocal.findInBackground(new FindCallback<ParseObject>() {
		    public void done(final List<ParseObject> offers, ParseException e) {
		        if (e == null) {
		        	
		        		Log.d("FB", "Ofertas Locales obtenidas");
		        		
		        		adapter = new OfferAdapter(HomeActivity.this, offers);
		        	    gridview.setAdapter(adapter);
		        	    
		    		    	progress.dismiss();
		    	    		
		        } else {
		            Log.d("FB", "Error: " + e.getMessage());
		            progress.dismiss();
		        }
		        
		        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offer");
	    			query.whereEqualTo("user", user);
	    			query.include("book");
	    		    	query.findInBackground(new FindCallback<ParseObject>() {
	
					@Override
					public void done(List<ParseObject> offersOnline,
							ParseException err) {
						if (err == null) {
							Log.d("FB", "Ofertas Online obtenidas");
						
							ParseObject.pinAllInBackground(offersOnline);
							
							for (ParseObject parseObjectOnline : offersOnline) {
								
								boolean exist = false;
								
								for (ParseObject parseObjectLocal : offers) {
									
									if (parseObjectLocal.getObjectId().equals( parseObjectOnline.getObjectId() ) ) {
										
										exist =  true;
										
									}
									
								}
								
								if(!exist){									
									adapter.add(parseObjectOnline);
								}
							}
							
						}
					}
	    		    		
				});
		        
		    }
		});
		
	}
	
}
