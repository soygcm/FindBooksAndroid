package me.mobileease.findbooks;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends ActionBarActivity implements OnClickListener {
	private EditText passwordInput;
	private EditText usernameInput;
	private Button loginButton;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewsAndSetListeners();
				
		ParseAnalytics.trackAppOpened(getIntent());
	}
	/// Obtener las vistas desde el layout y guardarlas en campos de la clase
	private void findViewsAndSetListeners() {
		usernameInput = (EditText) findViewById(R.id.login_username_input);
		passwordInput = (EditText) findViewById(R.id.login_password_input);
		loginButton = (Button) findViewById(R.id.parse_login_button);
	
		loginButton.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.parse_login_button) {
			
			Log.d("FB", "Tratando de hacer login");
			
			Toast.makeText(getApplicationContext(), "Tratando de hacer login", Toast.LENGTH_SHORT).show();
			
			login();
		}
		
	}
	private void login() {

		
		ParseUser.logInInBackground(usernameInput.getText().toString(), passwordInput.getText().toString(), new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					
					Toast.makeText(getApplicationContext(), "Login con exito: " + user.getUsername(), Toast.LENGTH_LONG).show();
					
				} else {
					
				}
			}

		});
		
	}
}
