package me.mobileease.findbooks;

import java.util.ArrayList;
import java.util.List;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PerfilActivity extends ActionBarActivity implements OnClickListener {

	public static final String USER_NEW = "userNew";
	private ParseUser currentUser;
	private TextView txtUsername;
	private TextView txtPhone;
	private TextView txtMail;
	private Button save;
	private TextView txtName;
	private boolean userNew;
	private ImageView imgPerfil;
	private AutoCompleteTextView place;
	private ImageView searchPlace;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadInterface();
	     
	    Intent intent = getIntent();
	    
	    userNew = intent.getBooleanExtra(USER_NEW, false);
	    
	    currentUser = ParseUser.getCurrentUser();
	    
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			getUserFacebookInfo();
		}
	    
		
	}
	
	private void loadInterface() {		
		setContentView(R.layout.activity_perfil);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {  		
        		setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        txtUsername = (TextView) findViewById(R.id.perfilUsuario);
        txtName = (TextView) findViewById(R.id.perfilNombre);
        txtPhone = (TextView) findViewById(R.id.perfilTelefono);
        txtMail = (TextView) findViewById(R.id.perfilCorreo);
        imgPerfil = (ImageView) findViewById(R.id.PerfilImagen);
        place = (AutoCompleteTextView) findViewById(R.id.place); 
        save = (Button) findViewById(R.id.save);
        searchPlace = (ImageView) findViewById(R.id.searchPlace);
        save.setOnClickListener(this);
        searchPlace.setOnClickListener(this);
        
        
        
	}

	private void getUserFacebookInfo() {
		
		Log.d(FindBooks.TAG, "get GraphUser");						

		String name = currentUser.getString("name");
		String email = currentUser.getString("email");
		String username = currentUser.getString("nickname");
		String phone = currentUser.getString("phone");
		
		setProfileImage(currentUser.getString("facebookId"));
		
		txtName.setText(name);
		txtMail.setText(email);
		txtUsername.setText(username);
		txtPhone.setText(phone);
      
		Session session = ParseFacebookUtils.getSession();
		
		if (session != null && session.isOpened() && userNew ) {
			
			
			Request request = Request.newMeRequest(session, new GraphUserCallback() {
				
				@Override
				public void onCompleted(final GraphUser user, Response response) {

					if (user != null){
						
						currentUser.put("facebookId", user.getId());
						currentUser.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								
								if(e == null){
									Log.d(FindBooks.TAG, "GraphUser and save facebookId");						
									String email = (String) user.getProperty("email");
									String name = (String) user.getName();
									setProfileImage(user.getId());
									
									txtName.setText(name);
									txtMail.setText(email);
								}else{
									Log.d(FindBooks.TAG, "error saving facebookId: "+ e.getLocalizedMessage());						
									
								}
								
							}
						});
						
					}else{
						Log.d(FindBooks.TAG, "No GraphUser "+ response.getError().getErrorMessage() );						

					}
					
					
				}
			});
			
			request.executeAsync();
			
		}
	}

	private void setProfileImage(String facebookId) {
		String imageURL = "https://graph.facebook.com/"+facebookId+"/picture?type=square&width=100&height=100";
		Ion.with(imgPerfil).load(imageURL);
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
		
		if(id == R.id.save){
			
			guardarPerfil();
			
		}
		
		if(id == R.id.searchPlace){
			
			searchPlace();
			
		}
	}

	private void searchPlace() {
		
		String address = place.getText().toString();
		String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+address+"&sensor=true";
		
//		Ion.with(this).load(url).set
		
		Ion.with(this)
		.load(url)
		.asJsonObject()
		.setCallback(new FutureCallback<JsonObject>() {
		   @Override
		    public void onCompleted(Exception e, JsonObject result) {
			   
			   if(e == null){
				   
				   JsonArray results = result.getAsJsonArray("results");
				   
				   List<PlaceResult> places = new ArrayList<PlaceResult>();
				   
				   for (JsonElement jsonElement : results) {
					   
					   PlaceResult place = new PlaceResult(jsonElement.getAsJsonObject());
					   
					   places.add(place);
					   
				   }
				   
				   Log.d(FindBooks.TAG, "searchPlace: "+ results.size());
				   
				   ArrayAdapter<PlaceResult> adapter = new ArrayAdapter<PlaceResult>(PerfilActivity.this, android.R.layout.simple_list_item_1, places);
				   place.setAdapter(adapter);
				   place.setText("");
				   place.showDropDown();
				   
			   }else{
				   
			   }

		   
		   	}
		});

//		
	}
	
	private class PlaceResult{
		
		

		private String name;
		private long lat;
		private long lng;
		private String countryCode;
		
		public PlaceResult(JsonObject jsonObject) {
			
			name = jsonObject.get("formatted_address").getAsString();
			JsonArray address_components = jsonObject.getAsJsonArray("address_components");
			
			JsonObject country = address_components.get(address_components.size()-1).getAsJsonObject();
			
			countryCode = country.get("short_name").getAsString();
			
		}

		public String getName() {
			return name;
		}

		public long getLat() {
			return lat;
		}

		public long getLng() {
			return lng;
		}

		public String getCountryCode() {
			return countryCode;
		}
		
		public String toString(){
			return this.name;
		}
		
	}

	private void guardarPerfil() {
		
		String name = txtName.getText().toString();
		String email = txtMail.getText().toString();
		String username = txtUsername.getText().toString();
		String phone = txtPhone.getText().toString();

		currentUser.put("name", name);
		currentUser.put("email", email);
		currentUser.put("nickname", username);
		currentUser.put("phone", phone);
		
		currentUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				
				if(e == null){
					
					if (userNew) {
						showHome();
					}else{						
						onBackPressed();
					}
					
				}else{
					Log.d(FindBooks.TAG, "erro: "+ e.getLocalizedMessage());
				}
				
			}
		});
		

	}

	protected void showHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

}


