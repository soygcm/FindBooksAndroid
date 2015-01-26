package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.List;

import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.model.MyBook;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TransactionsActivity extends ActionBarActivity {
	private ParseUser user;
	protected ListView listTransactions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transactions);
	    
	    user = ParseUser.getCurrentUser();
	    
	    listTransactions = (ListView) findViewById(R.id.transactionsList);
	    		
	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {  		
        		setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        if(user.getObjectId() != null){
        		getTransactions();
        }
        
	}

	private void getTransactions() {
		
		
		ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(MyBook.CLASS);
		userQuery.whereEqualTo("user", ParseUser.getCurrentUser() );
		
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
				if(e == null){
					TransactionAdapter adapterTransactions = new TransactionAdapter(TransactionsActivity.this, list);
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
	
	
//	{
//@Override
//public View getView(int position, View convertView, ViewGroup parent) {
//	View row;
//
//	mInflater = LayoutInflater.from(HomeActivity.this);
//	
//	if (null == convertView) {
//		row = mInflater.inflate(R.layout.list_item, null);
//	} else {
//		row = convertView;
//	}
//
//	TextView tv = (TextView) row.findViewById(android.R.id.text1);
//	tv.setText(getItem(position));
//
//	return row;
//}
//};
	
}
