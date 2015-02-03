package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.koushikdutta.ion.Ion;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TransactionActivity extends ActionBarActivity {
	
	public static final String OFFER_CONDITION = "condition";
	public static final String OFFER_BINDING = "binding";
	public static final String OFFER_PRICE = "price";
	public static final String OFFER_COMMENT = "comment";
	public static final String USER_NAME = "username";
	public static final String USER_PHONE = "phone";
	public static final String USER_MAIL = "mail";
	public static final String OFFERING = "offering";
	public static final String ACCEPTED = "accepted";
	private TextView txtTitle;
	private TextView txtAuthors;
	private TextView txtUsername;
	private TextView txtCondition;
	private TextView txtPrice;
	private ImageView imgBook;
	private String bookTitle;
	private String bookSubtitle;
	private String bookAuthors;
	private String bookImage;
	private String offerBinding;
	private String offerPrice;
	private String offerCondition;
	private String offerComment;
	private String userName;
	private String userPhone;
	private String userMail;
	private boolean offering;
	private boolean accepted;
	private TextView txtName;
	private TextView txtPhone;
	private TextView txtMail;
	private View perfilView;
	private Button btnWant;
	private ParseConfig config;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {  		
        		setSupportActionBar(toolbar);
        }
        
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		btnWant = (Button) findViewById(R.id.btnWant);
		btnWant.setVisibility(View.GONE);
		txtAuthors = (TextView) findViewById(R.id.txtAuthors);
		txtUsername = (TextView) findViewById(R.id.txtUsername);
		perfilView = (View) findViewById(R.id.perfilView);
		txtCondition = (TextView) findViewById(R.id.txtCondition);
		txtPrice = (TextView) findViewById(R.id.txtPrice);
		imgBook = (ImageView) findViewById(R.id.imgBook);
		txtName = (TextView) findViewById(R.id.txtName);
		txtPhone = (TextView) findViewById(R.id.txtPhone);
		txtMail = (TextView) findViewById(R.id.txtMail);

		Intent intent = getIntent();
		
		bookTitle = intent.getStringExtra(BookActivity.BOOK_TITLE);
		bookSubtitle = intent.getStringExtra(BookActivity.BOOK_SUBTITLE);
		bookAuthors = intent.getStringExtra(BookActivity.BOOK_AUTHORS);
		bookImage = intent.getStringExtra(BookActivity.BOOK_IMAGE);
		offerCondition = intent.getStringExtra(TransactionActivity.OFFER_CONDITION);
		offerBinding = intent.getStringExtra(TransactionActivity.OFFER_BINDING);
		offerPrice = intent.getStringExtra(TransactionActivity.OFFER_PRICE);
		offerComment = intent.getStringExtra(TransactionActivity.OFFER_COMMENT);
		userName = intent.getStringExtra(TransactionActivity.USER_NAME);
		userPhone = intent.getStringExtra(TransactionActivity.USER_PHONE);
		userMail = intent.getStringExtra(TransactionActivity.USER_MAIL);
		offering = intent.getBooleanExtra(TransactionActivity.OFFERING, false);
		accepted = intent.getBooleanExtra(TransactionActivity.ACCEPTED, false);

		if (bookImage != null) {
			Ion.with(imgBook).load(bookImage);
		}else{
			imgBook.setImageResource(android.R.color.transparent);
		}
		
		if (bookSubtitle == null) {
	    		txtTitle.setText( bookTitle );
		}else{			
			txtTitle.setText( Html.fromHtml(bookTitle+": <small>"+bookSubtitle+"</small>") );
		}
	
	    if(bookAuthors != null){
	    		txtAuthors.setText(bookAuthors);	    
	    }else{
	    		txtAuthors.setVisibility(View.GONE);
	    }
		
		if(offering){
			toolbar.setBackgroundColor(getResources().getColor(R.color.ui_magenta_oferta));
			perfilView.setVisibility(View.GONE);
			setTitle("EN TRANSACCION");
		}else{
			toolbar.setBackgroundColor(getResources().getColor(R.color.ui_menta_busqueda));
			txtUsername.setText(userName);
			setTitle("CASI LO TIENES");
		}
		
		getConfigConditionBindingComment();
		
		txtCondition.setText( Html.fromHtml(offerBinding+", "+offerCondition+", "+ offerComment) );
		txtPrice.setText(offerPrice);
		
		txtName.setText(userName);
		txtPhone.setText(userPhone);
		txtMail.setText(userMail);
		
	}
	
	private void getConfigConditionBindingComment() {

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
		    
		    conditionBinding();
		    
		  }
		});
		
	}

	protected void conditionBinding() {
		
		JSONArray mBookBookbinding = config.getJSONArray("MyBookBookbinding");
		JSONArray mBookCondition = config.getJSONArray("MyBookCondition");

		offerBinding = codeToName(mBookBookbinding, offerBinding);
		offerCondition = codeToName(mBookCondition, offerCondition);
		
		
		
		txtCondition.setText( Html.fromHtml(offerBinding+", "+offerCondition+", "+ offerComment) );
		
	}

	private String codeToName(JSONArray jsonList, String codeSearch) {

		String codeReturn = "";
		
		List<String> listName = JSONArrayToList(jsonList, "name");
		List<String> listCode = JSONArrayToList(jsonList, "code");

		for (String name : listName) {
			for (String code : listCode) {
				
				if(code.equals(codeSearch)){
					codeReturn = name;
				}
				
			}
		}
		
		return codeReturn;
	}

	private List<String> JSONArrayToList(JSONArray jsonArray, String key) {
		List<String> returnList = new ArrayList<String>();
		for (int i=0;i<jsonArray.length();i++){ 
			try {
				returnList.add(jsonArray.getJSONObject(i).getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return returnList;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		    onBackPressed();
		    return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
