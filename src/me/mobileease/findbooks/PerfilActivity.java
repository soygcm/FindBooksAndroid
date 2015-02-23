package me.mobileease.findbooks;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PerfilActivity extends ActionBarActivity implements OnClickListener {

	public static final String USER_NEW = "userNew";
	private ParseUser currentUser;
	private TextView txtUsername;
	private TextView txtPhone;
	private TextView txtMail;
	private Button save;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfil);
		
		loadInterface();
	     
	    Intent intent = getIntent();
	    
	    boolean userNew = intent.getBooleanExtra(USER_NEW, false);
	    
	    currentUser = ParseUser.getCurrentUser();
	    
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)
				
//				&& userNew 
				
				) {
			getUserFacebookInfo();
		}
	    
		
	}
	
	private void loadInterface() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {  		
        		setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        txtUsername = (TextView) findViewById(R.id.perfilNombre);
        txtPhone = (TextView) findViewById(R.id.perfilTelefono);
        txtMail = (TextView) findViewById(R.id.perfilCorreo);
        
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
	}

	private void getUserFacebookInfo() {
		
		Log.d(FindBooks.TAG, "get GraphUser");						


		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			
			
			Request request = Request.newMeRequest(session, new GraphUserCallback() {
				
				@Override
				public void onCompleted(GraphUser user, Response response) {

					if (user != null){	
						
						Log.d(FindBooks.TAG, "GraphUser");						
						String email = (String) user.getProperty("email");
						
						txtMail.setText(email);
						
					}else{
						Log.d(FindBooks.TAG, "No GraphUser "+ response.getError().getErrorMessage() );						

					}
					
					
				}
			});
			
			request.executeAsync();
			
		}
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
	}

	private void guardarPerfil() {

		
	}

}


