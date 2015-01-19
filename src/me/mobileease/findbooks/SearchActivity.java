package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.mobileease.findbooks.adapter.BookAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

public class SearchActivity extends ActionBarActivity implements OnItemClickListener {

	private EditText searchQuery;
	private ListView list;
	protected BookAdapter adapter;
	private ProgressDialog progress;
	private boolean searchFind;
	private boolean searchAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		list = (ListView) findViewById(R.id.resultList);
		
		list.setOnItemClickListener(this);
		
		Intent intent = getIntent();
		searchFind = intent.getBooleanExtra(HomeActivity.SEARCH_FIND, false);
		searchAdd = intent.getBooleanExtra(HomeActivity.SEARCH_ADD, false);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {  		
        		setSupportActionBar(toolbar);
        }
		
		ActionBar mActionBar = getSupportActionBar();
		
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
//		mActionBar.setDisplayShowHomeEnabled(false);
//		mActionBar.setDisplayShowTitleEnabled(false);
//		LayoutInflater mInflater = LayoutInflater.from(this);

//		View mCustomView = mInflater.inflate(R.layout.actionbar_search, null);
		
//		mActionBar.setCustomView(mCustomView);
//		mActionBar.setDisplayShowCustomEnabled(true);
		
		searchQuery = (EditText) findViewById(R.id.searchQuery);
//		mTitleTextView.setText("My Own Title");

		ImageButton imageButton = (ImageButton) findViewById(R.id.btnSearch);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
											
				
				getBooks();
				
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
	

	protected void getBooks() {
		
		progress = new ProgressDialog(this);
		progress.setTitle("Buscando");
		progress.setMessage("En unos segundos, te mostrar� los resultados de tu busqueda...");
		progress.show();
		
		if (adapter != null) {
			adapter.clear();
		}
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("query", searchQuery.getText().toString() );
		
		ParseCloud.callFunctionInBackground("search", params, new FunctionCallback<HashMap<String, Object>>() {
			

			@SuppressWarnings("unchecked")
			@Override
			public void done(HashMap<String, Object> hashmap, ParseException err) {
				
//				Log.d("FB", hashmap.getClass().getName() + ": " + iterador.next().getClass().getName() );
//				Log.d("FB", hashmap.toString() );
				
				if(err == null){
				
					Object models = hashmap.get("models");
					
					ArrayList<?> booksObj;
					List<ParseObject> books = new ArrayList<ParseObject>();;

					if (models instanceof ArrayList<?>) {
						if( ((ArrayList<?>)models).get(0) instanceof ParseObject){
							books = (List<ParseObject>) models;
						}
					}
					
					if (books.size()>0) {
						Log.d("FB", "setear Adaptador" );
						adapter = new BookAdapter(SearchActivity.this, books);
//						adapter.notifyDataSetChanged();
						list.setAdapter(adapter);
					}
	        	    		
				}else{
					
				}
				progress.dismiss();
				
			}
			});
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		if(searchAdd){
			showAddOffer(position);
		}else if(searchFind){
			showBook(position);
		}
		
	}



	private void showBook(int position) {
		
		ParseObject book = adapter.getItem(position);
		
		Intent intent = new Intent(SearchActivity.this, BookActivity.class);
	    String id = book.getObjectId();
	    intent.putExtra(BookActivity.BOOK_ID, id);
	    startActivity(intent);
		
	}



	private void showAddOffer(int position) {

		Intent intent = new Intent(SearchActivity.this, AddOfferActivity.class);
//	    EditText editText = (EditText) findViewById(R.id.edit_message);
//	    String message = editText.getText().toString();
//	    intent.putExtra(SEARCH_FIND, (position == 0) );
//	    intent.putExtra(SEARCH_ADD, (position == 1) );
		
		ParseObject book = adapter.getItem(position);
	    String id = book.getObjectId();
	    intent.putExtra(BookActivity.BOOK_ID, id);
		
	    startActivity(intent);
		
	}

	
}
