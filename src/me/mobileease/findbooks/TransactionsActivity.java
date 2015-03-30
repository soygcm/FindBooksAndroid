package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.List;

import me.mobileease.findbooks.adapter.TransactionAdapter;
import me.mobileease.findbooks.model.MyBook;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TransactionsActivity extends ActionBarActivity {
	private ParseUser user;
	protected ListView listTransactions;
	protected TransactionAdapter adapterTransactions;
	private View loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transactions);
		loading = (View) findViewById(R.id.loading);

		user = ParseUser.getCurrentUser();

		listTransactions = (ListView) findViewById(R.id.transactionsList);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (user.getObjectId() != null) {
			getTransactions();
		}

	}
	
	@Override
	protected void onResume() {

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		
		super.onResume();
	}

	private void getTransactions() {

		ParseQuery<ParseObject> userQuery = ParseQuery.getQuery(MyBook.CLASS);
		userQuery.whereEqualTo("user", ParseUser.getCurrentUser());

		ParseQuery<ParseObject> queryWant = ParseQuery.getQuery("Transaction");
		queryWant.whereMatchesQuery("bookWant", userQuery);
		queryWant.whereNotEqualTo("endedWant", true);

		ParseQuery<ParseObject> queryOffer = ParseQuery.getQuery("Transaction");
		queryOffer.whereMatchesQuery("bookOffer", userQuery);
		queryOffer.whereNotEqualTo("endedOffer", true);

		ArrayList<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();
		queryList.add(queryOffer);
		queryList.add(queryWant);
		ParseQuery<ParseObject> query = ParseQuery.or(queryList);

		
		query.include("bookWant");
		query.include("bookOffer");
		query.include("bookOffer.book");
		query.include("bookOffer.user");
		query.include("bookWant.user");
		
		loading.setVisibility(View.VISIBLE);
		
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				loading.setVisibility(View.GONE);

				
				if (e == null) {
					adapterTransactions = new TransactionAdapter(
							TransactionsActivity.this, list, true, true);
					listTransactions.setAdapter(adapterTransactions);
					listTransactions.setOnItemClickListener(adapterTransactions);

				}
			}

		});

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
//			onBackPressed();
			
			Intent upIntent = NavUtils.getParentActivityIntent(this);
	        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	            // This activity is NOT part of this app's task, so create a new task
	            // when navigating up, with a synthesized back stack.
	            TaskStackBuilder.create(this)
	                    // Add all of this activity's parents to the back stack
	                    .addNextIntentWithParentStack(upIntent)
	                    // Navigate up to the closest parent
	                    .startActivities();
	        } else {
	            // This activity is part of this app's task, so simply
	            // navigate up to the logical parent activity.
	        		onBackPressed();
//	            NavUtils.navigateUpTo(this, upIntent);
	        }
//	        return true;
			
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


}
