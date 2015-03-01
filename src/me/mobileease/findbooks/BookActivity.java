package me.mobileease.findbooks;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import me.mobileease.findbooks.adapter.BookOfferAdapter;
import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.helpers.FindBooksConfig;
import me.mobileease.findbooks.model.MyBook;
import me.mobileease.findbooks.views.BookView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
	private ProgressDialog progress;
	
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
	private String offerId;
	private Intent intent;
	private double offerPrice;

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

		intent = getIntent();
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		offerId = intent.getStringExtra(BookActivity.OFFER_ID);
		fromHome = intent.getBooleanExtra(BookActivity.FROM_HOME, false);
		bookType = intent.getStringExtra(BookActivity.BOOK_TYPE);
		bookTitle = intent.getStringExtra(BookActivity.BOOK_TITLE);
		bookAuthors = intent.getStringExtra(BookActivity.BOOK_AUTHORS);
		bookImage = intent.getStringExtra(BookActivity.BOOK_IMAGE);
		offerPrice = intent.getDoubleExtra(TransactionActivity.OFFER_PRICE, -1);
		
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
		authors.setText(bookAuthors);
		
		adapterTransactions = new TransactionAdapter(BookActivity.this,
				new ArrayList<ParseObject>(), false, false);
		list.setAdapter(adapterTransactions);

		
		setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		//Ofer info
		offerInfo();

		updateBackgroundSize();

		if (fromHome) {
			getTransactions();
		} else {
			knowUserWantBookAndGetOffers();
		}

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
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Falta el ACL??, para los permisos y posiblemente hacer query segun el
	 * usuario
	 */
	private void getTransactions() {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
		ParseObject book = ParseObject.createWithoutData("MyBook", offerId);

		boolean showOffer = false;
		
		if (bookType.equals("OFFER")) {
			query.whereEqualTo("bookOffer", book);
			query.whereNotEqualTo("endedOffer", true);
		} else if (bookType.equals("WANT")) {
			showOffer = true;
			query.whereEqualTo("bookWant", book);
			query.whereNotEqualTo("endedWant", true);
		}
		
		final boolean finalShowOffer = showOffer;

		query.include("bookOffer");
		query.include("bookWant");
		query.include("bookOffer.user");
		query.include("bookWant.user");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> trans, ParseException e) {
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

	/**
	 * Descubrir si este libro ya lo quiero, para evitar crear otro Want
	 * 
	 */
	private void knowUserWantBookAndGetOffers() {

		progress = new ProgressDialog(this);
		progress.setTitle("Obteniendo ofertas");
		progress.setMessage("Estoy buscando si existe alguna oferta disponible para este libro...");
		progress.show();

		ParseObject book = ParseObject.createWithoutData("Book", bookId);
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);
		query.whereEqualTo("book", book);
		query.whereEqualTo("type", "WANT");
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					if (list.size() > 0) {
						bookWant = list.get(0);
					}
				}

				getOffers();

			}

		});

	}

	private void getOffers() {

		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);

		Log.d("FB", "Libro: " + bookId);
		ParseObject book = ParseObject.createWithoutData("Book", bookId);
		query.whereEqualTo("book", book);
		query.whereEqualTo("type", "OFFER");
		query.include("book");
		query.include("user");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(final List<ParseObject> offers, ParseException e) {
				progress.dismiss();

				if (e == null) {

					Log.d("FB", "Ofertas: " + offers.size());
					adapterOffers = new BookOfferAdapter(BookActivity.this, offers,
							bookWant);
					list.setAdapter(adapterOffers);

					// adapter.addAll(offers);

				} else {
					Log.d("FB", "Error: " + e.getMessage());
				}

			}
		});

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
	}

	private void showEditOffer() {

		Intent intent = new Intent(this, AddOfferActivity.class);
		
		intent.putExtra(BookActivity.BOOK_ID, bookId);
		intent.putExtra(BookActivity.OFFER_ID, offerId);
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
		startActivity(intent);

	}

}
