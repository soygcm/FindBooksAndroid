package me.mobileease.findbooks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.fragment.FormOfferFragment;
import me.mobileease.findbooks.helpers.FindBooksConfig;
import me.mobileease.findbooks.helpers.FindBooksConfig.AddOfferOptions;
import me.mobileease.findbooks.model.MyBook;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddOfferActivity extends ActionBarActivity implements
		OnClickListener, OnItemSelectedListener {

	private ImageView mImageView;
	private Button takePhotoBtn;
	private Button btnSave;
	private String bookId;
	private ParseObject offer;
//	private Spinner currency;
	private Spinner condition;
	private Spinner bookbinding;
	private String bookTitle;
	private String bookAuthors;
	private String bookImage;
	private ImageView imgBook;
	private FrameLayout frameBackground;
	private TextView title;
	private TextView authors;
	private View header;
	private EditText price;
	private EditText comment;
	private Spinner offerType;
	private FindBooksConfig config;
	private Button save;
	private boolean editOffer;
	private String offerCondition;
	private String offerBinding;
	private double offerPrice;
	private String offerComment;
	private Intent intent;
	private List<AddOfferOptions> conditions;
	private List<AddOfferOptions> bookbindings;
//	private String offerCurrency;
	private String offerId;
	private TextView txtCurrency;
	private ProgressBar loading;
	
	public static final String EDIT = "edit";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_offer);
		
		loading = (ProgressBar) findViewById(R.id.loading);
		
		header = findViewById(R.id.header);
		mImageView = (ImageView) findViewById(R.id.photo);
		takePhotoBtn = (Button) findViewById(R.id.take_photo);
		btnSave = (Button) findViewById(R.id.btnSave);
		save = (Button) findViewById(R.id.save);
		offerType = (Spinner) findViewById(R.id.offerType);
//		currency = (Spinner) findViewById(R.id.currency);
		condition = (Spinner) findViewById(R.id.condition);
		bookbinding = (Spinner) findViewById(R.id.bookbinding);
		imgBook = (ImageView) findViewById(R.id.imgBook);
		frameBackground = (FrameLayout) findViewById(R.id.frameBackground);
		title = (TextView) findViewById(R.id.txtTitle);
		authors = (TextView) findViewById(R.id.txtAuthors);
		price = (EditText) findViewById(R.id.price);
		comment = (EditText) findViewById(R.id.comment);
		View offerView = (View) findViewById(R.id.offerView);
		offerView.setVisibility(View.GONE);
		txtCurrency = (TextView) findViewById(R.id.txtCurrency);
		
		intent = getIntent();
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		offerId = intent.getStringExtra(BookActivity.OFFER_ID);
		bookTitle = intent.getStringExtra(BookActivity.BOOK_TITLE);
		bookAuthors = intent.getStringExtra(BookActivity.BOOK_AUTHORS);
		bookImage = intent.getStringExtra(BookActivity.BOOK_IMAGE);

		editOffer = intent.getBooleanExtra(EDIT, false);
		
		offer = new ParseObject(MyBook.CLASS);
		
		Log.d(FindBooks.TAG, "imageLink: " + bookImage);
		if (bookImage != null) {
			Ion.with(imgBook).load(bookImage);
		} else {
			imgBook.setImageResource(android.R.color.transparent);
		}
		title.setText(bookTitle);
		authors.setText(bookAuthors);

		takePhotoBtn.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		save.setOnClickListener(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		
		setTitle(null);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		updateBackgroundSize();
		
		offerType.setOnItemSelectedListener(this);
		
		getConfigAdapters();
		
		ParseUser user = ParseUser.getCurrentUser();
		
		setCurrency(user.getString("currency"));
		
		if(editOffer){
			setParamsAndOfferId();
		}
		
	}

	private void setCurrency(String currencyCode) {
		
		Currency currency  = Currency.getInstance(currencyCode);
		
		String currencyString = currency.getSymbol() + " ("+ currency.getCurrencyCode() + ") ";
		
		
		txtCurrency.setText(currencyString);
		
		
	}
	
	private void setParamsAndOfferId() {
		
		offer = ParseObject.createWithoutData(MyBook.CLASS, offerId);
		
		// offerId (MyBook)
		offerCondition = intent.getStringExtra(TransactionActivity.OFFER_CONDITION);
		offerBinding = intent.getStringExtra(TransactionActivity.OFFER_BINDING);
		offerPrice = intent.getDoubleExtra(TransactionActivity.OFFER_PRICE, -1);
		offerComment = intent.getStringExtra(TransactionActivity.OFFER_COMMENT);		
		
		
		comment.setText(offerComment);
		
		if(offerPrice > 0){
			offerType.setSelection(1);
			price.setText(String.valueOf(offerPrice));
			
		}else{
			offerType.setSelection(0);
		}
		
		selectSpinnerItem(condition, conditions, offerCondition);
		selectSpinnerItem(bookbinding, bookbindings, offerBinding);

		
		/*
		 * price
		 * comment
		 * currency
		 * bookbinding
		 * condition
		 */
	}

	private void selectSpinnerItem(Spinner spinner,
			List<AddOfferOptions> listOfferOptions, String optionCode) {

		for (AddOfferOptions offerOption : listOfferOptions) {

			Log.d(FindBooks.TAG, "equals: "+ offerOption.getCode() + ", "+optionCode);
			if(offerOption.getCode().equals(optionCode)){
				int position = listOfferOptions.indexOf(offerOption);

				if(position != -1){	
					Log.d(FindBooks.TAG, "selection: "+ position);
					spinner.setSelection(position);				
				}
			}
			
		}
		
	}

	private void getConfigAdapters() {

		config = new FindBooksConfig();

		conditions = config.getMyBookConditionList();
		bookbindings = config.getMyBookBinding();
		
		Log.d(FindBooks.TAG, "condition.size: "+ conditions.size());
		
		List<String> currencies = new ArrayList<String>();
		currencies.add("₡");
		currencies.add("$");
		
		ArrayAdapter<AddOfferOptions> adapterCondition = new ArrayAdapter<AddOfferOptions>(this,
				android.R.layout.simple_spinner_item, conditions);
		ArrayAdapter<AddOfferOptions> adapterBookbinding = new ArrayAdapter<AddOfferOptions>(
				this, android.R.layout.simple_spinner_item, bookbindings);
		ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, currencies);

		adapterCondition
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterBookbinding
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterCurrency
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		condition.setAdapter(adapterCondition);
		bookbinding.setAdapter(adapterBookbinding);
//		currency.setAdapter(adapterCurrency);

		condition.setPrompt("En que condición está el libro");
		bookbinding.setPrompt("Encuadernación");

		condition.setOnItemSelectedListener(this);
		bookbinding.setOnItemSelectedListener(this);
//		currency.setOnItemSelectedListener(this);
		

//		currency.setSelection(0);
		bookbinding.setSelection(0);
		condition.setSelection(0);
	}

	protected void updateBackgroundSize() {

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

							int viewHeight = header.getHeight();

							setBackgroundSize(viewHeight);

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

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.take_photo) {
			dispatchTakePictureIntent();
		}
		if (id == R.id.save || id == R.id.btnSave) {
			saveOffer();
		}
	}

	private void saveOffer() {

		ParseObject book = ParseObject.createWithoutData("Book", bookId);

		offer.put("book", book);
		offer.put("type", "OFFER");
		offer.put("user", ParseUser.getCurrentUser());
		if (price.getText() != null) {

			try {
				BigDecimal priceDecimal = new BigDecimal(price.getText()
						.toString());
				if (priceDecimal != null) {
					offer.put("price", priceDecimal);
				}
			} catch (NumberFormatException ex) { // handle your exception
				ex.printStackTrace();
			}

		}
		offer.put("comment", comment.getText().toString());
		
		loading.setVisibility(View.VISIBLE);
		btnSave.setEnabled(false);
		
		offer.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				loading.setVisibility(View.GONE);
				btnSave.setEnabled(true);
				
				if (e == null) {
					Log.d("FB", "Oferta Guardada");
					showHome();
				} else {
					e.printStackTrace();
				}
			}
		});

	}

	protected void showHome() {
		
		
		setResult(HomeActivity.UPDATED);
		finish();
		
		
//		Intent intent = new Intent(this, HomeActivity.class);
//		startActivity(intent);
	}

	static final int REQUEST_IMAGE_CAPTURE = 1;
	public static final String OFFER_CURRENCY = "currency";

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			mImageView.setImageBitmap(imageBitmap);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {


		id = parent.getId();

		if (id == R.id.offerType) {

			if (position == 0) {
				price.setVisibility(View.GONE);
				txtCurrency.setVisibility(View.GONE);
			} else if (position == 1) {
				price.setVisibility(View.VISIBLE);
				txtCurrency.setVisibility(View.VISIBLE);
			}

		} else if (id == R.id.currency) {

			if (position == 0) {
				offer.put("currency", "CRC");
			} else if (position == 1) {
				offer.put("currency", "USD");
			}

		} else if (id == R.id.bookbinding) {
			AddOfferOptions option = bookbindings.get(position);
			offer.put("bookbinding", option.getCode());
		} else if (id == R.id.condition) {
			AddOfferOptions option = conditions.get(position);
			offer.put("condition", option.getCode());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
