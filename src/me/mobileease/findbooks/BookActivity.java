package me.mobileease.findbooks;

import java.util.List;

import me.mobileease.findbooks.adapter.BookOfferAdapter;
import me.mobileease.findbooks.model.MyBook;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class BookActivity extends ActionBarActivity {

	public static final String BOOK_ID = "book_id";
	public static final String FROM_HOME = "fromHome";
	public static final String BOOK_TYPE = "bookType";

	private ListView list;
	private String bookId;
	private ProgressDialog progress;
	protected BookOfferAdapter adapter;
	protected ParseObject bookWant;
	private boolean fromHome;
	private String bookType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		
		list = (ListView) findViewById(R.id.offerList);
		
//		list.setOnItemClickListener(this);
		
		Intent intent = getIntent();
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		fromHome = intent.getBooleanExtra(BookActivity.FROM_HOME, false);
		bookType = intent.getStringExtra(BookActivity.BOOK_TYPE);

		
		
		if(!bookId.isEmpty() && !fromHome){
			
			wantThisBook();
			
		}else if(fromHome){
			
			getTransactions();
			
		}
	
		
	}
	
	/**
	 * Falta el ACL, para los permisos y posiblemente
	 * hacer query segun el usuario
	 */
	private void getTransactions() {

		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
		ParseObject book = ParseObject.createWithoutData("MyBook", bookId);

		if(bookType.equals("OFFER") ){			
			query.whereEqualTo("bookOffer", book);
			Log.d(FindBooks.TAG, "OFFER,  bookId:"+ bookId);
		}else if(bookType.equals("WANT") ){
			query.whereEqualTo("bookWant", book);
			Log.d(FindBooks.TAG, "WANT,  bookId:"+ bookId);
		}

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> trans, ParseException e) {
				if(e == null){
					Log.d(FindBooks.TAG, "total: "+trans.size());
					ArrayAdapter<ParseObject> adapterTransactions = new ArrayAdapter<ParseObject>(BookActivity.this, android.R.layout.simple_list_item_1, trans);
					list.setAdapter(adapterTransactions);
				}else{
					Log.d(FindBooks.TAG, "error: "+ e.getMessage() );
				}
			}
			
			
			
		});
		
	}

	/**
	 * Descubrir si este libro ya lo quiero,
	 * para evitar crear otro Want
	 * 
	 */
	private void wantThisBook() {
		
		progress = new ProgressDialog(this);
		progress.setTitle("Obteniendo ofertas");
		progress.setMessage("Estoy buscando si existe alguna oferta disponible para este libro...");
		progress.show();

		ParseObject book = ParseObject.createWithoutData("Book", bookId);
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);
		query.whereEqualTo("book",  book);
		query.whereEqualTo("type", "WANT");
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if(e==null){
					if(list.size() > 0){						
						bookWant = list.get(0);
					}
				}
				
				getOffers();
				
			}
			
			
		});
		

	}


	private void getOffers() {
		
		
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);
		
		Log.d("FB", "Libro: "+ bookId);
		ParseObject book = ParseObject.createWithoutData("Book", bookId);
		query.whereEqualTo("book",  book);
		query.whereEqualTo("type", "OFFER");
		query.include("book");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(final List<ParseObject> offers, ParseException e) {
		    		progress.dismiss();
		    		
		    		
		    		if (e == null) {
		        			        		
		    			Log.d("FB", "Ofertas: "+ offers.size());
		        		adapter = new BookOfferAdapter(BookActivity.this, offers, bookWant);
		        	    list.setAdapter(adapter);
		        	    		    	    		
		        } else {
		            Log.d("FB", "Error: " + e.getMessage());
		        }
		        
		    }
		});
		
	}

	
}
