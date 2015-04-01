package me.mobileease.findbooks;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.mobileease.findbooks.adapter.BookOfferAdapter;
import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.helpers.FindBooksConfig;
import me.mobileease.findbooks.model.MyBook;
import me.mobileease.findbooks.views.BookView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BookActivity extends ActionBarActivity implements
	OnClickListener {

	public static final String BOOK_ID = "book_id";
	public static final String FROM_HOME = "fromHome";
	public static final String BOOK_TYPE = "bookType";
	public static final String BOOK_TITLE = "title";
	public static final String BOOK_AUTHORS = "authors";
	public static final String BOOK_IMAGE = "image";
	public static final String BOOK_SUBTITLE = "subtitle";
	public static final String OFFER_ID = "offerId";

	private ListView list;
	private String bookId;
//	private ProgressDialog progress;
	
	protected BookOfferAdapter adapterOffers;
	private TransactionAdapter adapterTransactions;
	
	protected ParseObject bookWant;
	private boolean fromHome;
	private String bookType;
	private ImageView imgBook;
	private FrameLayout frameBackground;
	private View header;
	private String bookTitle;
	private String bookAuthors;
	private String bookImage;
	private Button btnWant;
	private TextView txtUsername;
	private TextView txtCondition;
	private View perfilView;
	private TextView txtPrice;
	private View offerView;
	private FindBooksConfig config;
	private String offerCondition;
	private String offerBinding;
//	private double offerPrice;
	private String offerComment;
	private ImageButton btnEdit;
//	private String offerCurrency;
	private String myBookId;
	private Intent intent;
	private double offerPrice;
	private Button noOffer;
	private Button noWant;
	private int transactionCount;
	private Button shareIt;
	private String bookSubtitle;
	protected List<ParseObject> transactions;
	private ProgressBar loading;
	private View fondo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		
		loading = (ProgressBar) findViewById(R.id.loading);
		fondo = findViewById(R.id.fondo);
		
		list = (ListView) findViewById(R.id.offerList);
		imgBook = (ImageView) findViewById(R.id.imgBook);
		frameBackground = (FrameLayout) findViewById(R.id.frameBackground);

		LayoutInflater inflater = getLayoutInflater();
		header = inflater.inflate(R.layout.view_book, list, false);
		list.addHeaderView(header, null, false);

		TextView title = (TextView) header.findViewById(R.id.txtTitle);
		TextView authors = (TextView) header.findViewById(R.id.txtAuthors);
		
		//offer views
		btnWant = (Button) header.findViewById(R.id.btnWant);
		btnWant.setVisibility(View.GONE);
		perfilView = (View) header.findViewById(R.id.perfilView);
		perfilView.setVisibility(View.GONE);
		offerView = (View) header.findViewById(R.id.offerView);
		btnEdit = (ImageButton) header.findViewById(R.id.edit);
		btnEdit.setVisibility(View.VISIBLE);
		btnEdit.setOnClickListener(this);
		
		
		txtCondition = (TextView) header.findViewById(R.id.txtCondition);
		txtPrice = (TextView) header.findViewById(R.id.txtPrice);
		noWant = (Button) header.findViewById(R.id.noWant);
		noOffer = (Button) header.findViewById(R.id.noOffer);
		shareIt = (Button) header.findViewById(R.id.shareIt);

		intent = getIntent();
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		myBookId = intent.getStringExtra(BookActivity.OFFER_ID);
		fromHome = intent.getBooleanExtra(BookActivity.FROM_HOME, false);
		bookType = intent.getStringExtra(BookActivity.BOOK_TYPE);
		
		bookTitle = intent.getStringExtra(BookActivity.BOOK_TITLE);
		bookSubtitle = intent.getStringExtra(BookActivity.BOOK_SUBTITLE);
		
		bookAuthors = intent.getStringExtra(BookActivity.BOOK_AUTHORS);
		bookImage = intent.getStringExtra(BookActivity.BOOK_IMAGE);
		offerPrice = intent.getDoubleExtra(TransactionActivity.OFFER_PRICE, -1);
		transactionCount = intent.getIntExtra(TransactionActivity.OFFER_TRANSACTION_COUNT, 0);
		
		offerCondition = intent
				.getStringExtra(TransactionActivity.OFFER_CONDITION);
		offerBinding = intent.getStringExtra(TransactionActivity.OFFER_BINDING);
		offerComment = intent.getStringExtra(TransactionActivity.OFFER_COMMENT);
		
		Log.d(FindBooks.TAG, "imageLink: " + bookImage);
		if (bookImage != null) {
			Ion.with(imgBook).load(bookImage);
		} else {
			imgBook.setImageResource(android.R.color.transparent);
		}
		

		title.setText(bookTitle);
		
		if(bookAuthors == null){
			authors.setVisibility(View.GONE);
		}else{
			authors.setVisibility(View.VISIBLE);
			authors.setText(bookAuthors);
		}
		
		
		adapterTransactions = new TransactionAdapter(BookActivity.this,
				new ArrayList<ParseObject>(), false, false);
		list.setAdapter(adapterTransactions);

		
		setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(bookType != null){			
			if (bookType.equals("OFFER")) {
				fondo.setBackgroundColor(getResources().getColor(R.color.ui_fondo_magenta_40));
			}else if (bookType.equals("WANT")) {
				fondo.setBackgroundColor(getResources().getColor(R.color.ui_fondo_menta_40));
			}
		}
		
		//Ofer info
		offerInfo();

		updateBackgroundSize();

		if (fromHome) {
			getTransactions();
			
			noWantOfferInterface();
			
		} else {
			knowUserWantBookAndGetOffers();
		}

	}
	
	private void noWantOfferInterface() {
		
		
		if (bookType.equals("OFFER")) {
			noOffer.setVisibility(View.VISIBLE);			
		}else if (bookType.equals("WANT")) {
			noWant.setVisibility(View.VISIBLE);
		}
		
		noWant.setOnClickListener(this);
		noOffer.setOnClickListener(this);
		
	}

	protected void offerInfo() {
		
		
		
		config = new FindBooksConfig();
		
		if (bookType != null && bookType.equals("OFFER")) {
			
			MyBook myBook = new MyBook(intent);
			
			String offerPriceString = myBook.getPriceFormated();
			
			txtPrice.setText(offerPriceString);
			Log.d(FindBooks.TAG, offerBinding + ", " + offerCondition);
			String offerBindingString = config.getBindingLocalized(offerBinding);
			String offerConditionString = config.getConditionLocalized(offerCondition);
			txtCondition.setText(Html.fromHtml(offerBindingString + ", " + offerConditionString+ ", " + offerComment));
		}else{
			
			offerView.setVisibility(View.GONE);
			
		}
		


	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			
			if(adapterOffers != null 
				&& adapterOffers.transactions != null
				&& adapterOffers.transactions.size()>0){
				 setResult(HomeActivity.UPDATED);
			        finish();
			}else{
				onBackPressed();				
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Falta el ACL??, para los permisos y posiblemente hacer query segun el
	 * usuario
	 */
	private void getTransactions() {
		
		boolean showOffer = false;
		
		if (bookType.equals("WANT")) {
			showOffer = true;
		}
		
		final boolean finalShowOffer = showOffer;

		ParseQuery<ParseObject> query = getTransactionQuery(bookType);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> trans, ParseException e) {
				loading.setVisibility(View.GONE);
				if (e == null) {
					Log.d(FindBooks.TAG, "total: " + trans.size());
					adapterTransactions = new TransactionAdapter(
							BookActivity.this, trans, false, finalShowOffer);
					list.setAdapter(adapterTransactions);
					adapterTransactions.notifyDataSetChanged();
					list.setOnItemClickListener(adapterTransactions);

//					updateBackgroundSize();
				} else {
					Log.d(FindBooks.TAG, "error: " + e.getMessage());
				}
			}

		});

	}

	private ParseQuery<ParseObject> getTransactionQuery(String type) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
		ParseObject book = ParseObject.createWithoutData("MyBook", myBookId);

		
		if (type.equals("OFFER")) {
			query.whereEqualTo("bookOffer", book);
			query.whereNotEqualTo("endedOffer", true);
		} else if (type.equals("WANT")) {
			query.whereEqualTo("bookWant", book);
			query.whereNotEqualTo("endedWant", true);
		}
		

		query.include("bookOffer");
		query.include("bookWant");
		query.include("bookOffer.user");
		query.include("bookWant.user");
		return query;
	}

	/**
	 * Descubrir si este libro ya lo quiero, para evitar crear otro Want
	 * 
	 */
	private void knowUserWantBookAndGetOffers() {

//		progress = new ProgressDialog(this);
//		progress.setTitle("Obteniendo ofertas");
//		progress.setMessage("Estoy buscando si existe alguna oferta disponible para este libro...");
//		progress.show();

		ParseObject book = ParseObject.createWithoutData("Book", bookId);
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);
		query.whereEqualTo("book", book);
		query.whereNotEqualTo("deleted", true);
		query.whereEqualTo("type", "WANT");
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					if (list.size() > 0) {
						bookWant = list.get(0);
						myBookId = bookWant.getObjectId();
						
						getTransactionCurrent();
						
					}else{
						
						getOffers();
					}
				}else{
					
//					progress.dismiss();

					e.printStackTrace();
				}


			}

		});

	}

	/**
	 * Obtener las transacciónes que el usuario tiene en este momento, de este 
	 * bookWant, porque si existe un bookWant de este libro
	 * Y luego las ofertas
	 */
	protected void getTransactionCurrent() {

		ParseQuery<ParseObject> query = getTransactionQuery("WANT");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> trans, ParseException e) {
				if (e == null) {
					transactions = trans;
					getOffers();
				} else {
//					progress.dismiss();
					e.printStackTrace();
				}
			}
		});
	}

	private void getOffers() {

		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);

		Log.d("FB", "Libro: " + bookId);
		ParseObject book = ParseObject.createWithoutData("Book", bookId);
		query.whereEqualTo("book", book);
		query.whereEqualTo("type", "OFFER");
		query.whereNotEqualTo("deleted", true);
		query.include("book");
		query.include("user");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(final List<ParseObject> offers, ParseException e) {
//				progress.dismiss();
				loading.setVisibility(View.GONE);
				if (e == null) {
					if(offers.size() == 0){
						shareItQuestion();
						showStareItButton();
						Log.d(FindBooks.TAG, "No hay ofertas, dile a tus amigos, que estas buscando este libro.");						
					}else{
						adapterOffers = new BookOfferAdapter(BookActivity.this, offers,	bookWant, transactions);
						list.setAdapter(adapterOffers);
					}
					// adapter.addAll(offers);
				} else {
					e.printStackTrace();
				}

			}
		});

	}


	protected void showStareItButton() {
		shareIt.setVisibility(View.VISIBLE);
		shareIt.setOnClickListener(this);
	}

	protected void shareItQuestion() {
		

		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE){
					shareIt();
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("No hay ofertas disponibles, para este libro. ¿Deseas pedirle a tus amigos que te ayuden a encontrarlo?")
				.setPositiveButton("Si", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
				
	}

	protected void updateBackgroundSize() {

		// Log.d(FindBooks.TAG, "height: "+ header.getHeight());

		ViewTreeObserver viewTreeObserver = header.getViewTreeObserver();

		if (viewTreeObserver.isAlive()) {
			viewTreeObserver
					.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {

							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								header.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								header.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}

							int viewWidth = header.getWidth();
							int viewHeight = header.getHeight();

							setBackgroundSize(viewHeight);

							// header.getViewTreeObserver().removeOnGlobalLayoutListener(this);

							Log.d(FindBooks.TAG, "height: " + viewHeight);

						}
					});
		}

	}

	protected void setBackgroundSize(int viewHeight) {
		LayoutParams params = frameBackground.getLayoutParams();
		int toolbar = (int) getResources().getDimension(
				R.dimen.abc_action_bar_default_height_material);
		LayoutParams newParams = new FrameLayout.LayoutParams(params.width,
				viewHeight + toolbar);

		frameBackground.setLayoutParams(newParams);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		
		if(id == R.id.edit){
			showEditOffer();
		}
		if(id == R.id.noWant){
			confirmDeleteBook();
		}
		if(id == R.id.noOffer){
			confirmDeleteBook();
		}
		if(id == R.id.shareIt){
			shareIt();
		}
	}

	private void shareIt() {

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = "";
		
		
		if(bookAuthors == null){			
			shareBody = "Hola, ¿me ayudas a encontrar este libro? \"" + bookTitle +"\". Lo estoy buscando en http://findbooks.me/ pero nadie lo esta ofreciendo :( ";
		}else{			
			shareBody = "Hola, ¿me ayudas a encontrar este libro? \"" + bookTitle +" ("+bookAuthors+") \". Lo estoy buscando en http://findbooks.me/ pero nadie lo esta ofreciendo :( ";
		}
		
//		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Estoy buscando un libro en Findbooks");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
		
		
	}

	private void confirmDeleteBook() {
		
		if(transactionCount == 0){
			
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE){
						deleteBook();
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Deseas eliminar este libro?")
					.setPositiveButton("Si", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
			
		}else{
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Para eliminar este libro, todas las transacciones deben ser concluidas.").setPositiveButton("Ok", null).show();
			
		}
		
	}

	protected void deleteBook() {
		
		ParseObject book = ParseObject.createWithoutData("MyBook", myBookId);

		book.put("deleted", true);
		
		if(bookType != null 
			&& bookType.equals("OFFER") ) {
			book.put("delete", true);
		}
		
		loading.setVisibility(View.VISIBLE);
		noWant.setEnabled(false);
		noOffer.setEnabled(false);
		book.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				loading.setVisibility(View.GONE);
				noWant.setEnabled(true);
				noOffer.setEnabled(true);
				if(e == null){
					
					backHome();					
				}
				
			}
		});
		
		
	}
	
	protected void backHome() {

//		Intent intent = new Intent(this, HomeActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//		startActivity(intent);
		
		setResult(HomeActivity.UPDATED);
        finish();
		
	}

	private void showEditOffer() {

		Intent intent = new Intent(this, AddOfferActivity.class);
		
		intent.putExtra(BookActivity.BOOK_ID, bookId);
		intent.putExtra(BookActivity.OFFER_ID, myBookId);
		intent.putExtra(BookActivity.BOOK_TITLE, bookTitle);
		intent.putExtra(BookActivity.BOOK_AUTHORS, bookAuthors);
		intent.putExtra(AddOfferActivity.EDIT, true);
		intent.putExtra(BookActivity.BOOK_IMAGE, bookImage);
		
		intent.putExtra(TransactionActivity.OFFER_PRICE, offerPrice);
		intent.putExtra(TransactionActivity.OFFER_BINDING, offerBinding);
		intent.putExtra(TransactionActivity.OFFER_CONDITION, offerCondition);
		intent.putExtra(TransactionActivity.OFFER_COMMENT, offerComment);
		
		/*
		 * price
		 * comment
		 * currency
		 * bookbinding
		 * condition
		 * 			
		 */
		
		startActivityForResult(intent, HomeActivity.MY_BOOK);
		
//		startActivity(intent);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(resultCode == HomeActivity.UPDATED) {
	    		if (requestCode == HomeActivity.MY_BOOK
	    				|| requestCode == HomeActivity.TRANSACTION) {
	    			setResult(HomeActivity.UPDATED, data);
	    			finish();
	    		}
	    }
	    if(resultCode == TransactionsActivity.ACCEPTED && requestCode == HomeActivity.TRANSACTION) {
			getTransactions();
		}
	    
	}

}
