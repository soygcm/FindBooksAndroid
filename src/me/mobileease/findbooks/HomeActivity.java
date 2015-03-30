package me.mobileease.findbooks;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

import me.mobileease.findbooks.FindBooks.FirstConfigCallback;
import me.mobileease.findbooks.adapter.MyBookAdapter;
import me.mobileease.findbooks.model.MyBook;
import me.mobileease.findbooks.model.User;

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
import android.widget.ProgressBar;
import android.widget.TextView;

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
	private User user;
	private GridView gridview;
//	private ProgressDialog progress;
	protected List<ParseObject> userOffers;
	private MyBookAdapter adapter;
	private DrawerLayout drawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView listTransactions;
	private ImageButton btnTransactions;
	private TextView txtUsername;
	private ProgressBar loading;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		user = User.getCurrentUser();

		// cargar la vista grilla
		gridview = (GridView) findViewById(R.id.gridview);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		loading = (ProgressBar) findViewById(R.id.loading);
		
		// drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		// listTransactions =(ListView) findViewById(R.id.transactionList);

		// mDrawerToggle = new ActionBarDrawerToggle(this, drawer, 0,
		// R.string.app_name );
		// drawer.setDrawerListener(mDrawerToggle);
		//
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true); //este aparece
		// el icono
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		txtUsername = (TextView) findViewById(R.id.perfil);
		txtUsername.setText(user.getNickname());
		btnTransactions = (ImageButton) findViewById(R.id.btnTransactions);
		btnTransactions.setOnClickListener(this);
		
		
		if (user.isValidLogin()){
			FindBooks.firstConfig(new FirstConfigCallback() {
				@Override
				public void done(Exception e) {		
					if(e == null){
						getMyBooks();						
					}else{	
						e.printStackTrace();
					}
				}
			});
		}else{
			Log.d("FB", "No hay usuario?");			
		}

	}

	/**
	 * Obtener las ofertas disponibles (no prestados o en transaccion) del
	 * usuario
	 */
	private void getMyBooks() {

//		progress = new ProgressDialog(this);
//		progress.setTitle("Cargando tus libros");
//		progress.setMessage("Dame un momento, estoy apunto de mostrarte tus libros...");
//		progress.show();

		Log.d("FB", "Obteniendo MyBooks");

		ParseQuery<ParseObject> query = ParseQuery.getQuery(MyBook.CLASS);
		query.whereEqualTo("user", user.getParseUser());
		query.whereNotEqualTo("deleted", true);
		query.include("book");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> offersOnline, ParseException err) {

//				progress.dismiss();
				
				loading.setVisibility(View.GONE);

				if (err == null) {

					adapter = new MyBookAdapter(HomeActivity.this, offersOnline);
					gridview.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					// set clicklistener
					gridview.setOnItemClickListener(adapter);

					

				}
			}

		});


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
					startLoginActivity();

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

	protected void startLoginActivity() {

		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(intent);
		
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
