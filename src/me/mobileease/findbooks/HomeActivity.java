package me.mobileease.findbooks;

import java.util.List;

import me.mobileease.findbooks.adapter.MyBookAdapter;
import me.mobileease.findbooks.model.MyBook;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.ConfigCallback;
import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class HomeActivity extends ActionBarActivity implements OnClickListener {
	public static final String SEARCH_ADD = "search_add";
	public static final String SEARCH_FIND = "search_find";
	private ParseUser user;
	private GridView gridview;
	private ProgressDialog progress;
	protected List<ParseObject> userOffers;
	private MyBookAdapter adapter;
	private DrawerLayout drawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView listTransactions;
	private ImageButton btnTransactions;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		user = ParseUser.getCurrentUser();

		// cargar la vista grilla
		gridview = (GridView) findViewById(R.id.gridview);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		// drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		// listTransactions =(ListView) findViewById(R.id.transactionList);

		// mDrawerToggle = new ActionBarDrawerToggle(this, drawer, 0,
		// R.string.app_name );
		// drawer.setDrawerListener(mDrawerToggle);
		//
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true); //este aparece
		// el icono
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		btnTransactions = (ImageButton) findViewById(R.id.btnTransactions);
		btnTransactions.setOnClickListener(this);
		// set clicklistener
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				if (position != 0) {

					showBook(position);

				}

			}
		});

		if (user.getObjectId() != null) {
			getOffers();
		} else {
			Log.d("FB", "No hay usuario?");
		}
		
		Log.d("TAG", "Getting the latest config...");
		ParseConfig.getInBackground(new ConfigCallback() {
			@Override
			public void done(ParseConfig config, ParseException e) {
				if (e == null) {
					Log.d("TAG", "Yay! Config was fetched from the server.");
				} else {
					Log.e("TAG", "Failed to fetch. Using Cached Config.");
				}
			}
		});

	}

	protected void showBook(int position) {

		ParseObject mBook = adapter.getItem(position - 1);

		Intent intent = new Intent(HomeActivity.this, BookActivity.class);
		String id = mBook.getObjectId();
		String type = mBook.getString("type");
		ParseObject book = mBook.getParseObject("book");
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
		intent.putExtra(BookActivity.FROM_HOME, true);
		intent.putExtra(BookActivity.BOOK_TYPE, type);
		intent.putExtra(BookActivity.BOOK_TITLE, title);
		intent.putExtra(BookActivity.BOOK_AUTHORS, authors);
		intent.putExtra(BookActivity.BOOK_IMAGE, imageLink);

		startActivity(intent);

	}

	/**
	 * Obtener las ofertas disponibles (no prestados o en transaccion) del
	 * usuario
	 */
	private void getOffers() {

		progress = new ProgressDialog(this);
		progress.setTitle("Cargando tus libros");
		progress.setMessage("Dame un momento, estoy apunto de mostrarte tus libros...");
		progress.show();

		Log.d("FB", "Obteniendo Ofertas");

		// ParseQuery<ParseObject> queryLocal =
		// ParseQuery.getQuery(MyBook.CLASS);
		// queryLocal.whereEqualTo("user", user);
		// queryLocal.fromLocalDatastore();
		// queryLocal.include("book");
		// queryLocal.findInBackground(new FindCallback<ParseObject>() {
		// public void done(final List<ParseObject> offers, ParseException e) {
		// if (e == null) {
		//
		// Log.d("FB", "Ofertas Locales obtenidas: "+offers.size());
		//
		// adapter = new OfferAdapter(HomeActivity.this, offers);
		// gridview.setAdapter(adapter);
		//
		// if (offers.size() != 0) {
		//
		// progress.dismiss();
		//
		// }
		//
		// } else {
		// Log.d("FB", "Error: " + e.getMessage());
		// progress.dismiss();
		// }

		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);
		query.whereEqualTo("user", user);
		query.include("book");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> offersOnline, ParseException err) {

				progress.dismiss();

				if (err == null) {

					adapter = new MyBookAdapter(HomeActivity.this, offersOnline);
					gridview.setAdapter(adapter);

					// Log.d("FB", "Ofertas Online obtenidas");
					//
					// ParseObject.pinAllInBackground(offersOnline);
					//
					// for (ParseObject parseObjectOnline : offersOnline) {
					//
					// boolean exist = false;
					//
					// for (ParseObject parseObjectLocal : offers) {
					//
					// if (parseObjectLocal.getObjectId().equals(
					// parseObjectOnline.getObjectId() ) ) {
					//
					// exist = true;
					//
					// }
					//
					// }
					//
					// if(!exist){
					// adapter.add(parseObjectOnline);
					// }
					// }

				}
			}

		});

		// }
		// });

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.btnTransactions) {

			showTransactions();

		}

	}

	private void showTransactions() {
		Intent intent = new Intent(HomeActivity.this,
				TransactionsActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.edit_profile) {
			editProfile();
		} else if (id == R.id.logout) {
			logout();
		}

		return super.onOptionsItemSelected(item);
	}

	private void logout() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:

					// Log the user out
					// ParseFacebookUtils.getSession().closeAndClearTokenInformation();
					ParseUser.logOut();

					// ((RespiremosSalud)
					// getApplication()).clearApplicationData();
					// Go to the login view
					finish();
					// startLoginActivity();

					break;
				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Deseas cerrar sesión")
				.setPositiveButton("Si", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();

	}

	private void editProfile() {

		Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
		startActivity(intent);

	}

	// @Override
	// protected void onPostCreate(Bundle savedInstanceState) {
	// super.onPostCreate(savedInstanceState);
	//
	// mDrawerToggle.syncState();
	// }
	//
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	//
	// mDrawerToggle.onConfigurationChanged(newConfig);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if (mDrawerToggle.onOptionsItemSelected(item)) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

}
