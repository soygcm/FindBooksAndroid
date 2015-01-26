package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import me.mobileease.findbooks.adapter.BookOfferAdapter;
import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.model.MyBook;
import me.mobileease.findbooks.views.BookView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class BookActivity extends ActionBarActivity {

	public static final String BOOK_ID = "book_id";
	public static final String FROM_HOME = "fromHome";
	public static final String BOOK_TYPE = "bookType";
	public static final String BOOK_TITLE = "title";
	public static final String BOOK_AUTHORS = "authors";
	public static final String BOOK_IMAGE = "image";

	private ListView list;
	private String bookId;
	private ProgressDialog progress;
	protected BookOfferAdapter adapter;
	protected ParseObject bookWant;
	private boolean fromHome;
	private String bookType;
	private ImageView imgBook;
	private FrameLayout frameBackground;
	private View header;
	private TransactionAdapter adapterTransactions;
	private String bookTitle;
	private String bookAuthors;
	private String bookImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {  		
        		setSupportActionBar(toolbar);
        }
		
		list = (ListView) findViewById(R.id.offerList);
		imgBook = (ImageView) findViewById(R.id.imgBook);
		frameBackground = (FrameLayout) findViewById(R.id.frameBackground);
		
		LayoutInflater inflater = getLayoutInflater();
		header = inflater.inflate(R.layout.view_book, list, false);
		list.addHeaderView(header, null, false);
		
		TextView title = (TextView) header.findViewById(R.id.txtTitle);
		TextView authors = (TextView) header.findViewById(R.id.txtAuthors);
		
//		list.setOnItemClickListener(this);
		
		Intent intent = getIntent();
		
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		fromHome = intent.getBooleanExtra(BookActivity.FROM_HOME, false);
		bookType = intent.getStringExtra(BookActivity.BOOK_TYPE);
		bookTitle = intent.getStringExtra(BookActivity.BOOK_TITLE);
		bookAuthors = intent.getStringExtra(BookActivity.BOOK_AUTHORS);
		bookImage = intent.getStringExtra(BookActivity.BOOK_IMAGE);
		Log.d(FindBooks.TAG , "imageLink: "+ bookImage);
		if (bookImage != null) {
			Ion.with(imgBook).load(bookImage);
		}else{
			imgBook.setImageResource(android.R.color.transparent);
		}
		
		title.setText(bookTitle);
		authors.setText(bookAuthors);
		
		adapterTransactions = new TransactionAdapter(BookActivity.this, new ArrayList<ParseObject>());
		list.setAdapter(adapterTransactions);
		
	    updateBackgroundSize();
		
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
		
		query.include("bookOffer");
		query.include("bookWant");
		query.include("bookOffer.user");
		query.include("bookWant.user");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> trans, ParseException e) {
				if(e == null){
					Log.d(FindBooks.TAG, "total: "+trans.size());
					TransactionAdapter adapterTransactions = new TransactionAdapter(BookActivity.this, trans);
					list.setAdapter(adapterTransactions);
					updateBackgroundSize();
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
		query.include("user");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(final List<ParseObject> offers, ParseException e) {
		    		progress.dismiss();
		    		
		    		
		    		if (e == null) {
		        			        		
		    			Log.d("FB", "Ofertas: "+ offers.size());
		        		adapter = new BookOfferAdapter(BookActivity.this, offers, bookWant);
		        	    list.setAdapter(adapter);
		    			
//		    			adapter.addAll(offers);
		    			
		        } else {
		            Log.d("FB", "Error: " + e.getMessage());
		        }
		        
		    }
		});
		
	}

	protected void updateBackgroundSize() {
		
//		Log.d(FindBooks.TAG, "height: "+ header.getHeight());

		ViewTreeObserver viewTreeObserver = header.getViewTreeObserver();
		
		if (viewTreeObserver.isAlive()) {
		  viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		    	
		    	if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
		    		header.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		    	} else {
		    		header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		    	}
		    	
		    	int viewWidth = header.getWidth();
	    		int viewHeight = header.getHeight();
	    		
		    	setBackgroundSize(viewHeight);
    
//		    		header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		    		
		    		Log.d(FindBooks.TAG, "height: "+ viewHeight);
		    		
		    }
		  });
		}
		
		
	}

	protected void setBackgroundSize(int viewHeight) {
		LayoutParams params = frameBackground.getLayoutParams();	
		int toolbar =(int) getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
		LayoutParams newParams = new FrameLayout.LayoutParams(params.width, viewHeight+toolbar);
		
		frameBackground.setLayoutParams(newParams);
	}

	
}
