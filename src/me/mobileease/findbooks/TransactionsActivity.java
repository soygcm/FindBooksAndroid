package me.mobileease.findbooks;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.model.MyBook;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

public class TransactionsActivity extends ActionBarActivity implements
		OnItemClickListener {
	private ParseUser user;
	protected ListView listTransactions;
	protected TransactionAdapter adapterTransactions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transactions);

		user = ParseUser.getCurrentUser();

		listTransactions = (ListView) findViewById(R.id.transactionsList);
		listTransactions.setOnItemClickListener(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (user.getObjectId() != null) {
			getTransactions();
		}

	}

	private void getTransactions() {

		ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(MyBook.CLASS);
		userQuery.whereEqualTo("user", ParseUser.getCurrentUser());

		ParseQuery<ParseObject> queryWant = ParseQuery.getQuery("Transaction");
		queryWant.whereMatchesQuery("bookWant", userQuery);

		ParseQuery<ParseObject> queryOffer = ParseQuery.getQuery("Transaction");
		queryOffer.whereMatchesQuery("bookOffer", userQuery);

		ArrayList<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();
		queryList.add(queryOffer);
		queryList.add(queryWant);
		ParseQuery<ParseObject> query = ParseQuery.or(queryList);

		query.include("bookWant");
		query.include("bookOffer");
		query.include("bookOffer.book");
		query.include("bookOffer.user");
		query.include("bookWant.user");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					adapterTransactions = new TransactionAdapter(
							TransactionsActivity.this, list);
					listTransactions.setAdapter(adapterTransactions);
				}
			}

		});

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

		showTransaction(position);

	}

	/**
	 * BookOffer: Titulo, Subtitulo, autores, foto MyBook: FotoOferta, Estado,
	 * Descripción, precio, moneda, binding User-Other: Usuario, telefono, etc.
	 */
	private void showTransaction(int position) {

		Intent intent = new Intent(TransactionsActivity.this,
				TransactionActivity.class);

		ParseObject transaction = adapterTransactions.getItem(position);
		boolean accepted = transaction.getBoolean("accepted");
		ParseObject bookOffer = transaction.getParseObject("bookOffer");
		ParseObject bookWant = transaction.getParseObject("bookWant");
		ParseObject book = bookOffer.getParseObject("book");
		ParseUser userOffer = bookOffer.getParseUser("user");
		ParseUser userWant = bookWant.getParseUser("user");
		String transactionId = transaction.getObjectId();
		String title = book.getString("title");
		String subtitle = book.getString("subtitle");
		
		//offer
		String offerCondition = bookOffer.getString("condition");
		String offerBinding = bookOffer.getString("bookBinding");
		String offerComment = bookOffer.getString("comment");
		String offerCurrency = bookOffer.getString("currency");
		Double price = bookOffer.getDouble("price");
		String offerPrice = "";
		if (price != 0) {
			NumberFormat format = NumberFormat.getCurrencyInstance();
			if (offerCurrency != null) {
				Currency currency = Currency.getInstance(offerCurrency);
				format.setCurrency(currency);
			}
			offerPrice = format.format(price);
		} else {
			offerPrice = "gratis";
		}
		
		Log.d(FindBooks.TAG, "price: "+offerPrice);

		String userPhone;
		String userName;
		String userMail;

		boolean offering = user.getObjectId().equals(userOffer.getObjectId());
		if (offering) {
			userName = userWant.getString("username");
			userPhone = userWant.getString("phone");
			userMail = userWant.getString("email");
		} else {
			userName = userOffer.getString("username");
			userPhone = userOffer.getString("phone");
			userMail = userOffer.getString("email");
		}

		List<String> authorsList = book.getList("authors");
		String authors = TextUtils.join(", ", authorsList);
		JSONObject image = book.getJSONObject("imageLinks");
		String imageLink = null;

		if (image != null) {
			try {
				imageLink = image.getString("thumbnail");
				// imageLink = imageLink.replaceAll("zoom=[^&]+","zoom=" + 4);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		intent.putExtra(BookActivity.BOOK_TITLE, title);
		intent.putExtra(BookActivity.BOOK_SUBTITLE, subtitle);
		intent.putExtra(BookActivity.BOOK_AUTHORS, authors);
		intent.putExtra(BookActivity.BOOK_IMAGE, imageLink);
		intent.putExtra(TransactionActivity.TRANSACTION_ID, transactionId);
		
		intent.putExtra(TransactionActivity.OFFER_CONDITION, offerCondition);
		intent.putExtra(TransactionActivity.OFFER_PRICE, offerPrice);
		intent.putExtra(TransactionActivity.OFFER_COMMENT, offerComment);
		intent.putExtra(TransactionActivity.OFFER_BINDING, offerBinding);
		
		intent.putExtra(TransactionActivity.USER_NAME, userName);
		intent.putExtra(TransactionActivity.USER_PHONE, userPhone);
		intent.putExtra(TransactionActivity.USER_MAIL, userMail);
		intent.putExtra(TransactionActivity.OFFERING, offering);
		intent.putExtra(TransactionActivity.ACCEPTED, accepted);

		//
		startActivity(intent);
	}

	// {
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// View row;
	//
	// mInflater = LayoutInflater.from(HomeActivity.this);
	//
	// if (null == convertView) {
	// row = mInflater.inflate(R.layout.list_item, null);
	// } else {
	// row = convertView;
	// }
	//
	// TextView tv = (TextView) row.findViewById(android.R.id.text1);
	// tv.setText(getItem(position));
	//
	// return row;
	// }
	// };

}
