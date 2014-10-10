package me.mobileease.findbooks;

import java.util.List;

import me.mobileease.findbooks.adapter.OfferAdapter;
import android.app.ProgressDialog;
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

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.home);
	    
	    user = ParseUser.getCurrentUser();

	    //cargar la vista grilla
	    gridview = (GridView) findViewById(R.id.gridview);

	    //set clicklistener
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(HomeActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    if(user != null){
	    		
	    		
	    	
		    getOffers();
	    }else{
            Log.d("FB", "No hay usuario?");
	    }
	    
	    
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
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Offer");
		query.whereEqualTo("user", user);
		query.fromLocalDatastore();
		query.include("book");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> offers, ParseException e) {
		        if (e == null) {
		        	
		        		Log.d("FB", "Ofertas obtenidas");
		        		
		        		/// Siempre despues de cada carga de internet		        	
//		        		ParseObject.pinAllInBackground(offers);
		        		
		    	    		gridview.setAdapter(new OfferAdapter(HomeActivity.this, offers));
		        		
		    		    	progress.dismiss();
		    	    		
		        } else {
		            Log.d("FB", "Error: " + e.getMessage());
		            progress.dismiss();
		        }
		    }
		});
		
	}
	
}
