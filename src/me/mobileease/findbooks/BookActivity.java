package me.mobileease.findbooks;

import java.util.List;

import me.mobileease.findbooks.adapter.BookOfferAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BookActivity extends ActionBarActivity {

	public static final String BOOK_ID = "book_id";
	private ListView list;
	private String bookId;
	private ProgressDialog progress;
	protected BookOfferAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		
		list = (ListView) findViewById(R.id.offerList);
		
//		list.setOnItemClickListener(this);
		
		Intent intent = getIntent();
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		
		if(!bookId.isEmpty()){
			
			getOffers();
			
		}
	
		
	}
	

	private void getOffers() {
		
		progress = new ProgressDialog(this);
		progress.setTitle("Obteniendo ofertas");
		progress.setMessage("Estoy buscando si existe alguna oferta disponible para este libro...");
		progress.show();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Offer");
		
		Log.d("FB", "Libro: "+ bookId);
		ParseObject book = ParseObject.createWithoutData("Book", bookId);
		query.whereEqualTo("book",  book);
		query.include("book");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(final List<ParseObject> offers, ParseException e) {
		    		progress.dismiss();
		    		
		    		
		    		if (e == null) {
		        			        		
		    			Log.d("FB", "Ofertas: "+ offers.size());
		        		adapter = new BookOfferAdapter(BookActivity.this, offers);
		        	    list.setAdapter(adapter);
		        	    		    	    		
		        } else {
		            Log.d("FB", "Error: " + e.getMessage());
		        }
		        
		    }
		});
		
	}

	
}
