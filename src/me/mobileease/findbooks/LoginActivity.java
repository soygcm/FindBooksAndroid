package me.mobileease.findbooks;

import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.ParseFacebookUtils.Permissions;

public class LoginActivity extends ActionBarActivity implements OnClickListener {
	private EditText passwordInput;
	private EditText usernameInput;
	private Button loginButton;
	private ProgressDialog progress;
	private Button signupButton;
	private Button facebookLogin;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (ParseUser.getCurrentUser().getObjectId() != null) {
			showHome();
		}

		loadInterface();

		ParseAnalytics.trackAppOpened(getIntent());
	}

	// / Obtener las vistas desde el layout y guardarlas en campos de la clase
	private void loadInterface() {
		usernameInput = (EditText) findViewById(R.id.login_username_input);
		passwordInput = (EditText) findViewById(R.id.login_password_input);
		loginButton = (Button) findViewById(R.id.parse_login_button);
		signupButton = (Button) findViewById(R.id.parse_signup_button);
		facebookLogin = (Button) findViewById(R.id.facebook_login);

		facebookLogin.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		signupButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.parse_login_button) {
			login();
		}
		if (id == R.id.parse_signup_button) {
			signup();
		}
		if(id == R.id.facebook_login){
			facebookLogin();
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void facebookLogin() {
		
		List<String> permissions = Arrays.asList("public_profile", "user_location",
				Permissions.Friends.ABOUT_ME);

		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			  @Override
			  public void done(ParseUser user, ParseException err) {
			    if (user == null) {
			    	Log.d(FindBooks.TAG,
							"Uh oh. The user cancelled the Facebook login. "+err.getMessage());
			    } else if (user.isNew()) {
			      Log.d("MyApp", "User signed up and logged in through Facebook!");
			      //Ver Perfil
			      user.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						// TODO Auto-generated method stub
					      showPerfil();
					}
				});
			    } else {
			      Log.d("MyApp", "User logged in through Facebook!");
			      //Ver Home
			      showHome();
			    }
			  }
			});
		
	}

	protected void showPerfil() {

		Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
		intent.putExtra(PerfilActivity.USER_NEW, true);
		startActivity(intent);
	}

	private void signup() {
		Log.d("FB", "Tratando de hacer login");

		progress = new ProgressDialog(this);
		progress.setTitle("Iniciando");
		progress.setMessage("En unos segundos estaras dentro de FindBooks...");
		progress.show();

		ParseUser user = new ParseUser();
		user.setUsername(usernameInput.getText().toString());
		user.setPassword(passwordInput.getText().toString());

		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				progress.dismiss();
				if (e == null) {
					Log.d("FB", "Login con exito..");
					showHome();
				} else {
					Log.d("FB", "Error: " + e.getMessage());
				}
			}
		});
	}

	private void login() {

		Log.d("FB", "Tratando de hacer login");

		progress = new ProgressDialog(this);
		progress.setTitle("Iniciando");
		progress.setMessage("En unos segundos estaras dentro de FindBooks...");
		progress.show();

		ParseUser.logInInBackground(usernameInput.getText().toString(),
				passwordInput.getText().toString(), new LogInCallback() {

					@Override
					public void done(ParseUser user, ParseException e) {
						progress.dismiss();

						if (user != null) {

							Log.d("FB", "Login con exito..");

							showHome();

						} else {
							Log.d("FB", "Error: " + e.getMessage());
						}
					}

				});

	}

	// / Mostrar el Home
	protected void showHome() {
		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
		startActivity(intent);
	}
}
