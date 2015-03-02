package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.List;

import me.mobileease.findbooks.model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;

import com.koushikdutta.ion.Ion;
import com.parse.ConfigCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TransactionActivity extends ActionBarActivity implements OnClickListener {

	public static final String OFFER_CONDITION = "condition";
	public static final String OFFER_BINDING = "binding";
	public static final String OFFER_PRICE = "price";
	public static final String OFFER_COMMENT = "comment";
	public static final String USER_NAME = "nickname";
	public static final String USER_PHONE = "phone";
	public static final String USER_MAIL = "mail";
	public static final String OFFERING = "offering";
	public static final String ACCEPTED = "accepted";
	public static final String TRANSACTION_ID = "transactionId";
	public static final String TRANSACTION_END_WANT = "endedWant";
	public static final String TRANSACTION_END_OFFER = "endedOffer";
	public static final String OFFER_TRANSACTION_COUNT = "transactionCount";
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
	private View profileView;
	private Button btnAccept;
	private Button btnCancel;
	private Button btnConclude;
	private TextView txtMessageTransaction;
	private String transactionId;
	private Toolbar toolbar;
	private Intent intent;
	private boolean endedWant;
	private boolean endedOffer;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		btnWant = (Button) findViewById(R.id.btnWant);
		btnWant.setVisibility(View.GONE);
		txtUsername = (TextView) findViewById(R.id.txtUsername);
		txtCondition = (TextView) findViewById(R.id.txtCondition);
		perfilView = (View) findViewById(R.id.perfilView);
		txtPrice = (TextView) findViewById(R.id.txtPrice);
		txtAuthors = (TextView) findViewById(R.id.txtAuthors);
		profileView = (View) findViewById(R.id.profileView);
		imgBook = (ImageView) findViewById(R.id.imgBook);
		txtName = (TextView) findViewById(R.id.txtName);
		txtPhone = (TextView) findViewById(R.id.txtPhone);
		txtMail = (TextView) findViewById(R.id.txtMail);
		btnAccept = (Button) findViewById(R.id.btnAccept);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConclude = (Button) findViewById(R.id.btnConclude);
		txtMessageTransaction = (TextView) findViewById(R.id.txtMessageTransaction);
		btnAccept.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnConclude.setOnClickListener(this);
		
		intent = getIntent();

		bookTitle = intent.getStringExtra(BookActivity.BOOK_TITLE);
		bookSubtitle = intent.getStringExtra(BookActivity.BOOK_SUBTITLE);
		bookAuthors = intent.getStringExtra(BookActivity.BOOK_AUTHORS);
		bookImage = intent.getStringExtra(BookActivity.BOOK_IMAGE);
		
		offerCondition = intent
				.getStringExtra(TransactionActivity.OFFER_CONDITION);
		offerBinding = intent.getStringExtra(TransactionActivity.OFFER_BINDING);
		offerPrice = intent.getStringExtra(TransactionActivity.OFFER_PRICE);
		offerComment = intent.getStringExtra(TransactionActivity.OFFER_COMMENT);
		
		userName = intent.getStringExtra(TransactionActivity.USER_NAME);
		userPhone = intent.getStringExtra(TransactionActivity.USER_PHONE);
		userMail = intent.getStringExtra(TransactionActivity.USER_MAIL);
		offering = intent.getBooleanExtra(TransactionActivity.OFFERING, false);
		accepted = intent.getBooleanExtra(TransactionActivity.ACCEPTED, false);
		transactionId = intent.getStringExtra(TransactionActivity.TRANSACTION_ID);

		endedWant = intent.getBooleanExtra(TransactionActivity.TRANSACTION_END_WANT, false);
		endedOffer = intent.getBooleanExtra(TransactionActivity.TRANSACTION_END_OFFER, false);
		
		/**
		 * BookInfo
		 */
		if (bookImage != null) {
			Ion.with(imgBook).load(bookImage);
		} else {
			imgBook.setImageResource(android.R.color.transparent);
		}

		if (bookSubtitle == null) {
			txtTitle.setText(bookTitle);
		} else {
			txtTitle.setText(Html.fromHtml(bookTitle + ": <small>"
					+ bookSubtitle + "</small>"));
		}

		if (bookAuthors != null) {
			txtAuthors.setText(bookAuthors);
		} else {
			txtAuthors.setVisibility(View.GONE);
		}
		
		
		transactionInfo();

		
		/**
		 * Offer Info
		 */
		txtCondition.setText(Html.fromHtml(offerBinding + ", " + offerCondition
				+ ", " + offerComment));

		txtPrice.setText(offerPrice.toString());
		
		getConfigConditionBindingComment();

		
		/**
		 * Contact info
		 */
		txtName.setText(userName);
		txtPhone.setText(userPhone);
		txtMail.setText(userMail);

		

	}

	private void transactionInfo() {

		/**
		 * Transaction info
		 */
		
		Transaction transactionModel = new Transaction(intent);
		String messageString = transactionModel.getMessage(false, false);
		txtMessageTransaction.setText(Html.fromHtml(messageString.toUpperCase()));
		
		if (offering) {
			toolbar.setBackgroundColor(getResources().getColor(
					R.color.ui_magenta_oferta));
			perfilView.setVisibility(View.GONE);
			
			if(accepted){
				btnAccept.setVisibility(View.GONE);
				btnCancel.setVisibility(View.GONE);
				btnConclude.setVisibility(View.VISIBLE);
				
			}else{
				btnAccept.setVisibility(View.VISIBLE);
				btnCancel.setVisibility(View.VISIBLE);
				btnConclude.setVisibility(View.GONE);
				
			}
		} else {
			toolbar.setBackgroundColor(getResources().getColor(
					R.color.ui_menta_busqueda));
			txtUsername.setText(userName);			
			btnAccept.setVisibility(View.GONE);
			
			if(accepted){
				btnCancel.setVisibility(View.GONE);
				btnConclude.setVisibility(View.VISIBLE);
				
			}else{
				btnConclude.setVisibility(View.GONE);
				btnCancel.setVisibility(View.VISIBLE);
				profileView.setVisibility(View.GONE);
				
			}
		}
		
		if(endedOffer || endedWant){
			toolbar.setBackgroundColor(getResources().getColor(
					R.color.ui_boton_rojo));
			profileView.setVisibility(View.GONE);
			btnAccept.setVisibility(View.GONE);
		}else{
			if(accepted){
				
				
			}else{
				
				
			}
		}
		
		
	}

	private void getConfigConditionBindingComment() {

		config = ParseConfig.getCurrentConfig();
//		Log.d("TAG", "Getting the latest config...");
//		ParseConfig.getInBackground(new ConfigCallback() {
//			@Override
//			public void done(ParseConfig config, ParseException e) {
//				if (e == null) {
//					Log.d("TAG", "Yay! Config was fetched from the server.");
//				} else {
//					Log.e("TAG", "Failed to fetch. Using Cached Config.");
//				}
//
//				config = ParseConfig.getCurrentConfig();

				conditionBinding();

//			}
//		});

	}

	protected void conditionBinding() {

		JSONArray mBookBookbinding = config.getJSONArray("MyBookBookbinding");
		JSONArray mBookCondition = config.getJSONArray("MyBookCondition");

		offerBinding = codeToName(mBookBookbinding, offerBinding);
		offerCondition = codeToName(mBookCondition, offerCondition);

		txtCondition.setText(Html.fromHtml(offerBinding + ", " + offerCondition
				+ ", " + offerComment));

	}

	static public String codeToName(JSONArray jsonList, String codeSearch) {

		String codeReturn = "";

		List<String> listName = JSONArrayToList(jsonList, "name");
		List<String> listCode = JSONArrayToList(jsonList, "code");

		for (String name : listName) {
			for (String code : listCode) {

				if (code.equals(codeSearch)) {
					codeReturn = name;
				}

			}
		}

		return codeReturn;
	}

	static public List<String> JSONArrayToList(JSONArray jsonArray, String key) {
		List<String> returnList = new ArrayList<String>();
		for (int i = 0; i < jsonArray.length(); i++) {
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

	@Override
	public void onClick(View v) {

		int id = v.getId();
		
		if(id == R.id.btnAccept){
			setAcepted();
		}else if (id == R.id.btnCancel){
			endTransaction();
		}else if (id == R.id.btnConclude){
			endTransaction();
		}
		
	}

	private void endTransaction() {

		Intent intent = new Intent(TransactionActivity.this,
				EndTransactionActivity.class);
		
		intent.putExtra(TransactionActivity.TRANSACTION_ID, transactionId);
		intent.putExtra(TransactionActivity.OFFERING, offering);
		intent.putExtra(TransactionActivity.ACCEPTED, accepted);
		
		
		startActivity(intent);
		
	}

	private void setAcepted() {
	
		ParseObject transaction = ParseObject.createWithoutData("Transaction", transactionId);
		Log.d(FindBooks.TAG, transactionId);
		transaction.put("accepted", true);
		transaction.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {

				if (e == null){
					Log.d(FindBooks.TAG, "transaccion aceptada");
					
					accepted = true;
					
					transactionInfo();
					
				}else{
					Log.d(FindBooks.TAG, "error: "+ e.getLocalizedMessage());
				}
			}
		});
		
	}

}
