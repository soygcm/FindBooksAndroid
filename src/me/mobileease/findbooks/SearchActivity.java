package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.mobileease.findbooks.adapter.BookAdapter;
import me.mobileease.findbooks.model.MyBook;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SearchActivity extends ActionBarActivity implements
		OnItemClickListener {

	private EditText searchQuery;
	private ListView list;
	protected BookAdapter adapter;
	private boolean searchFind;
	private boolean searchAdd;
	private View loading;
	private ImageButton btnSearch;
	private boolean updated;
	private TextView txtLastOffers;
	private String typeSearch;
	private ParseQuery<ParseObject> queryLastOffers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		loading = (View) findViewById(R.id.loading);

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

		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		txtLastOffers = (TextView) findViewById(R.id.txtLastOffers);

		if (searchFind) {
			toolbar.setBackgroundColor(getResources().getColor(
					R.color.ui_menta_busqueda));
			btnSearch.setImageResource(R.drawable.ic_buscar_blanco);
			typeSearch = MyBook.WANT;
			txtLastOffers.setText("Ofertas recientes");
			
			getLastBooks("offersCount");
			
		} else if (searchAdd) {
			toolbar.setBackgroundColor(getResources().getColor(
					R.color.ui_magenta_oferta));
			btnSearch.setImageResource(R.drawable.ic_agregar_blanco);
			typeSearch = MyBook.OFFER;
			txtLastOffers.setText("\"Lo quiero\" recientes");
			
			getLastBooks("wantsCount");

		}

		searchQuery = (EditText) findViewById(R.id.searchQuery);
		// mTitleTextView.setText("My Own Title");

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String query = searchQuery.getText().toString();
				if (!query.isEmpty()){					
					getBooks();
				}

			}
		});

	}


	private void getLastBooks(String counter) {
		loading.setVisibility(View.VISIBLE);
		

		queryLastOffers = ParseQuery.getQuery("Book");
		queryLastOffers.orderByDescending("updatedAt");
		queryLastOffers.setLimit(50);
		queryLastOffers.whereGreaterThan(counter, 0);
		queryLastOffers.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> l, ParseException e) {
				
				loading.setVisibility(View.GONE);
				
				if(e==null){
					
					if(adapter == null){
						adapter = new BookAdapter(SearchActivity.this, l, searchFind);
						list.setAdapter(adapter);
						txtLastOffers.setVisibility(View.VISIBLE);
						// adapter.notifyDataSetChanged();
					}
					
				}else{
					e.printStackTrace();
				}
				
			}
		});
		
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			
			if(updated){
				setResult(HomeActivity.UPDATED);
			    finish();
			}else{
				onBackPressed();				
			}
			
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void getBooks() {
		
		loading.setVisibility(View.VISIBLE);
		btnSearch.setEnabled(false);
		
		txtLastOffers.setVisibility(View.GONE);
		
		if(queryLastOffers != null){
			queryLastOffers.cancel();
		}
		
		if (adapter != null) {
			adapter.clear();
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("query", searchQuery.getText().toString());

		ParseCloud.callFunctionInBackground("search", params,
				new FunctionCallback<HashMap<String, Object>>() {

					@SuppressWarnings("unchecked")
					@Override
					public void done(HashMap<String, Object> hashmap,
							ParseException err) {

						// Log.d("FB", hashmap.getClass().getName() + ": " +
						// iterador.next().getClass().getName() );
						// Log.d("FB", hashmap.toString() );
						
						loading.setVisibility(View.GONE);
						btnSearch.setEnabled(true);

						if (err == null) {

							Object models = hashmap.get("models");

							ArrayList<?> booksObj;
							List<ParseObject> books = new ArrayList<ParseObject>();
							

							
							if (models instanceof ArrayList<?>) {
								if( ((ArrayList<?>) models).size() > 0){
									if (((ArrayList<?>) models).get(0) instanceof ParseObject) {
										books = (List<ParseObject>) models;
									}
								}
							}

							if (books.size() > 0) {
								Log.d("FB", "setear Adaptador");
								
								addButtonNoBook();
								
								adapter = new BookAdapter(SearchActivity.this,
										books, searchFind);
								// adapter.notifyDataSetChanged();
								list.setAdapter(adapter);
								
								
							}

						} else {
							
							
							Context context = getApplicationContext();
							CharSequence text = "Losiento, algo salio mal.";
							int duration = Toast.LENGTH_SHORT;

							Toast toast = Toast.makeText(context, text, duration);
							toast.show();
							
							err.printStackTrace();
						}

					}
				});
	}

	protected void addButtonNoBook() {

		LayoutInflater inflater = getLayoutInflater();
		View button = inflater.inflate(R.layout.button_feedback_nobook, list, false);

		Button buttonNoBook = (Button) button.findViewById(R.id.btnNoBook);
		
		buttonNoBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				feedbackNoBook();
				
			}
		});
		
		list.addFooterView(button);
		
	}

	protected void feedbackNoBook() {

		ParseObject feedback = new ParseObject("Feedback");
		
		feedback.put("feedback", searchQuery.getText().toString());
		feedback.put("type", "NO_BOOK");
		feedback.put("context", typeSearch);
		feedback.put("user", ParseUser.getCurrentUser());
		feedback.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				
				if(e == null){
					
					AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
					builder.setMessage("Gracias por tu feedback. en la próxima actualización agregaremos más libros. no dudes en contactarnos info@mobileease.me")
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                //do things
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
					
				}else{
					e.printStackTrace();
				}
				
			}
		});
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		if (searchAdd) {
			showAddOffer(position);
		} else if (searchFind) {
			showBook(position);
		}

	}

	private void showBook(int position) {
		Intent intent = new Intent(SearchActivity.this, BookActivity.class);

		ParseObject book = adapter.getItem(position);

		String id = book.getObjectId();
		String title = book.getString("title");
		String subtitle = book.getString("subtitle");
		List<String> authorsList = book.getList("authors");
		
		if (authorsList != null) {
			String authors = TextUtils.join(", ", authorsList);
			intent.putExtra(BookActivity.BOOK_AUTHORS, authors);
		}
		
		JSONObject image = book.getJSONObject("imageLinks");
		String imageLink = null;

		if (image != null) {
			try {
				imageLink = image.getString("thumbnail");
				imageLink = imageLink.replaceAll("zoom=[^&]+", "zoom=" + 4);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		intent.putExtra(BookActivity.BOOK_ID, id);
		intent.putExtra(BookActivity.BOOK_TITLE, title);
		intent.putExtra(BookActivity.BOOK_SUBTITLE, subtitle);
		intent.putExtra(BookActivity.BOOK_IMAGE, imageLink);
		intent.putExtra(BookActivity.BOOK_TYPE, "WANT");

		startActivityForResult(intent, HomeActivity.ADD_BOOK_WANT);

//		startActivity(intent);

	}

	private void showAddOffer(int position) {
		Intent intent = new Intent(SearchActivity.this, AddOfferActivity.class);

		ParseObject book = adapter.getItem(position);

		String id = book.getObjectId();
		String title = book.getString("title");
		List<String> authorsList = book.getList("authors");
		String authors = TextUtils.join(", ", authorsList);
		JSONObject image = book.getJSONObject("imageLinks");
		String imageLink = null;

		if (image != null) {
			try {
				imageLink = image.getString("thumbnail");
				imageLink = imageLink.replaceAll("zoom=[^&]+", "zoom=" + 4);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		intent.putExtra(BookActivity.BOOK_ID, id);
		intent.putExtra(BookActivity.BOOK_TITLE, title);
		intent.putExtra(BookActivity.BOOK_AUTHORS, authors);
		intent.putExtra(BookActivity.BOOK_IMAGE, imageLink);
		
		startActivityForResult(intent, HomeActivity.ADD_BOOK_OFFER);
//		startActivity(intent);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if( resultCode == HomeActivity.UPDATED) {
	    		if (requestCode == HomeActivity.ADD_BOOK_OFFER ) {
	    			setResult(HomeActivity.UPDATED, data);
	    			finish();
	        }
	    		if(requestCode == HomeActivity.ADD_BOOK_WANT){
	    			updated = true;
	    		}
	    }
	    if(resultCode == HomeActivity.NEW && 
	    		requestCode == HomeActivity.ADD_BOOK_WANT){
	    		setResult(HomeActivity.UPDATED, data);
			finish();
	    }
	    
	}

}
