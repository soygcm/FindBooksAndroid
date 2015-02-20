package me.mobileease.findbooks;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.mobileease.findbooks.adapter.EndOptionAdapter;
import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.helpers.FindBooksConfig;
import me.mobileease.findbooks.helpers.FindBooksConfig.EndOption;
import me.mobileease.findbooks.model.MyBook;

import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EndTransactionActivity extends ActionBarActivity implements
		OnItemClickListener {
	private static final String END_CODE = "endCode";
	private ParseUser user;
	protected ListView list;
	private FindBooksConfig config;
	private boolean offering;
	private boolean accepted;
	private String endCodeParent;
	private String transactionId;
	private String endOption;
	private EndOptionAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_transaction);

		user = ParseUser.getCurrentUser();
		
		config = new FindBooksConfig();

		list = (ListView) findViewById(R.id.transactionsList);
		list.setOnItemClickListener(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();

		transactionId = intent.getStringExtra(TransactionActivity.TRANSACTION_ID);
		offering = intent.getBooleanExtra(TransactionActivity.OFFERING, false);
		accepted = intent.getBooleanExtra(TransactionActivity.ACCEPTED, false);
		
		endOption = null;
		
		if(offering){
			if(accepted){
				endOption = "concludeUserOffer";
			}else{
				endOption = "cancelUserOffer";
			}
		}else{
			if(accepted){
				endOption = "concludeUserWant";
			}else{
				endOption = "cancelUserWant";
			}
		}
		
		getOptions();

	}

	private void getOptions() {
		
		List<EndOption> optionsList = config.getTransactionEndedOptionList(endOption);
		
		Log.d(FindBooks.TAG, "optionslist: "+ optionsList.size());
		
		adapter = new EndOptionAdapter(this, optionsList);
		
		list.setAdapter(adapter);

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
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		EndOption option = adapter.getItem(position);
		
		if(option.isTitle()){
			
		}else{
			endTransaction(option);
		}

	}
	
	private void endTransaction(EndOption option){
		
		//if offering?? Eliminar oferta?? haaa que success debo setear
		
		//Ultima confirmación si asi desea calificar la oferta y 
		// decirle que todo se desaparecera, etc, etc
		//Transaction transaction = new Transaction(transactionId);
		ParseObject transaction = ParseObject.createWithoutData("Transaction", transactionId);
		String userType;
		
		if(offering){
			userType = "Offer";
		}else{
			userType = "Want";
		}
		
		transaction.put("success"+userType, option.isSuccess());
		transaction.put("ended"+userType, true);
		transaction.put("detail"+userType, option.getCode());
		
		transaction.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {

				if (e == null){
					Log.d(FindBooks.TAG, "transaccion finalizada");
					
					accepted = true;
					backHome();
//					preguntarSiDeseaEliminarOferta()
					
				}else{
					Log.d(FindBooks.TAG, "error: "+ e.getLocalizedMessage());
				}
			}
		});
		
		
	}

	protected void backHome() {

		Intent intent = new Intent(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(intent);
		
	}

}
