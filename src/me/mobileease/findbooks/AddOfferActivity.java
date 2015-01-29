package me.mobileease.findbooks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.fragment.FormOfferFragment;
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

public class AddOfferActivity extends ActionBarActivity implements OnClickListener, OnItemSelectedListener{

	private ImageView mImageView;
	private Button takePhotoBtn;
	private Button btnSave;
	private String bookId;
	private ParseObject offer;
	private Spinner currency;
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
	private ParseConfig config;
	private Button save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_offer);
		
		offer = new ParseObject(MyBook.CLASS);

		header = findViewById(R.id.header);
		mImageView = (ImageView) findViewById(R.id.photo);
		takePhotoBtn = (Button) findViewById(R.id.take_photo);
		btnSave = (Button) findViewById(R.id.btnSave);
		save = (Button) findViewById(R.id.save);
		offerType = (Spinner) findViewById(R.id.offerType);
		currency = (Spinner) findViewById(R.id.currency);
		condition = (Spinner) findViewById(R.id.condition);
		bookbinding = (Spinner) findViewById(R.id.bookbinding);
		imgBook = (ImageView) findViewById(R.id.imgBook);
		frameBackground = (FrameLayout) findViewById(R.id.frameBackground);
		title = (TextView) findViewById(R.id.txtTitle);
		authors = (TextView) findViewById(R.id.txtAuthors);
		price = (EditText) findViewById(R.id.price);
		comment = (EditText) findViewById(R.id.comment);

		Intent intent = getIntent();
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
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
		
		takePhotoBtn.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		save.setOnClickListener(this);
		
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {  		
        		setSupportActionBar(toolbar);
        }
		
        setTitle(null);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		updateBackgroundSize();
		
		offerType.setOnItemSelectedListener(this);
		
		config = ParseConfig.getCurrentConfig();

		Log.d("TAG", "Getting the latest config...");
		ParseConfig.getInBackground(new ConfigCallback() {

		@Override
		  public void done(ParseConfig config, ParseException e) {
		    if (e == null) {
		      Log.d("TAG", "Yay! Config was fetched from the server.");
		    } else {
		      Log.e("TAG", "Failed to fetch. Using Cached Config.");
		    }
		    
			config = ParseConfig.getCurrentConfig();
		    
		    setAdapters();
		    
		  }
		});

    }
	
	protected void setAdapters() {

		JSONArray mBookBookbinding = config.getJSONArray("MyBookBookbinding");
		JSONArray mBookCondition = config.getJSONArray("MyBookCondition");
		
		List<String> conditions = new ArrayList<String>();
		for (int i=0;i<mBookCondition.length();i++){ 
			try {
				conditions.add(mBookCondition.getJSONObject(i).getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		List<String> bookbindings = new ArrayList<String>();
		for (int i=0;i<mBookBookbinding.length();i++){ 
			try {
				bookbindings.add(mBookBookbinding.getJSONObject(i).getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		List<String> currencies = new ArrayList<String>();
		currencies.add("₡");
		currencies.add("$");
		
		ArrayAdapter<String> adapterCondition = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, conditions);
		ArrayAdapter<String> adapterBookbinding = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bookbindings);
		ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);

		adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterBookbinding.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		condition.setAdapter(adapterCondition);
		bookbinding.setAdapter(adapterBookbinding);
		currency.setAdapter(adapterCurrency);
		
		condition.setOnItemSelectedListener(this);
		bookbinding.setOnItemSelectedListener(this);
		currency.setOnItemSelectedListener(this);

	}

	protected void updateBackgroundSize() {
		
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
		    	
	    		int viewHeight = header.getHeight();
	    		
		    	setBackgroundSize(viewHeight);
    		    				    		
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
		if(id == R.id.take_photo){
			dispatchTakePictureIntent();
		}
		if(id == R.id.save || id == R.id.btnSave){
			saveOffer();
		}
	}
	
	private void saveOffer() {
		
		BigDecimal priceDecimal = new BigDecimal(price.getText().toString());

		ParseObject book = ParseObject.createWithoutData("Book", bookId);

		offer.put("book", book );
		offer.put("type", "OFFER");
		offer.put("user", ParseUser.getCurrentUser());
		if(priceDecimal != null){			
			offer.put("price", priceDecimal);
		}
		offer.put("comment", comment.getText().toString() );
		offer.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null){
					
					Log.d("FB", "Oferta Guardada");
					
					showHome();
					
				}else{
					
				}
			}
		});
		
	}

	protected void showHome() {
		Intent intent = new Intent(this, HomeActivity.class);
	    startActivity(intent);
	}

	static final int REQUEST_IMAGE_CAPTURE = 1;

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
		
		JSONArray mBookBookbinding = config.getJSONArray("MyBookBookbinding");
		JSONArray mBookCondition = config.getJSONArray("MyBookCondition");
		
		List<String> conditionsCode = new ArrayList<String>();
		for (int i=0;i<mBookCondition.length();i++){ 
			try {
				conditionsCode.add(mBookCondition.getJSONObject(i).getString("code"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		List<String> bookbindingsCode = new ArrayList<String>();
		for (int i=0;i<mBookBookbinding.length();i++){ 
			try {
				bookbindingsCode.add(mBookBookbinding.getJSONObject(i).getString("code"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		id = parent.getId();
		
		if(id == R.id.offerType){
			
			if(position == 0){
				price.setVisibility(View.GONE);
				currency.setVisibility(View.GONE);
			}else if (position == 1){
				price.setVisibility(View.VISIBLE);
				currency.setVisibility(View.VISIBLE);
			}
			
		}else if(id == R.id.currency){
			
			if(position == 0){
				offer.put("currency", "CRC");
			}else if (position == 1){
				offer.put("currency", "USD");
			}
			
		}else if(id == R.id.bookbinding){
			offer.put("bookbinding", bookbindingsCode.get(position) );
		}else if(id == R.id.condition){
			offer.put("condition", conditionsCode.get(position) );
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

}
