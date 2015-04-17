package me.mobileease.findbooks;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

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

		ParseAnalytics.trackAppOpened(getIntent());
		
		if (ParseUser.getCurrentUser().getObjectId() != null) {
			showHome();
		}else{
			
			
			
			
			
		}

		loadInterface();
		
//		try {
//		    PackageInfo info = getPackageManager().getPackageInfo(
//		            "com.yours.package", 
//		            PackageManager.GET_SIGNATURES);
//		    for (Signature signature : info.signatures) {
//		        MessageDigest md = MessageDigest.getInstance("SHA");
//		        md.update(signature.toByteArray());
//		        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//		        }
//		} catch (NameNotFoundException e) {
//	        Log.d("KeyHash:", "NameNotFoundException");
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//	        Log.d("KeyHash:", "NoSuchAlgorithmException");
//			e.printStackTrace();
//		} catch (Exception e) {
//	        Log.d("KeyHash:", "Exception");
//			e.printStackTrace();
//		}

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
	    
	  
	  if(progress == null && !progress.isShowing()){			  
		progress = new ProgressDialog(this);
		progress.setTitle("Iniciando con Facebook");
		progress.setMessage("En unos segundos estaras dentro de FindBooks...");
		progress.show();
	  }
	  
	}

	private void facebookLogin() {
		
		List<String> permissions = Arrays.asList("public_profile", "user_location",
				Permissions.Friends.ABOUT_ME);

		progress = new ProgressDialog(this);
		progress.setTitle("Iniciando con Facebook");
		progress.setMessage("En unos segundos estaras dentro de FindBooks...");
		progress.show();
		
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			  @Override
			  public void done(ParseUser user, ParseException err) {
				  
				if(progress != null && progress.isShowing()){			  
		    			progress.dismiss();
		    			
		    		}
				  
			    if (user == null) {
			    		Log.d(FindBooks.TAG,
							"Uh oh. The user cancelled the Facebook login. ");
			    		
			    		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
						builder.setMessage("Lo siento, Algo salió mal al iniciar sesión con Facebook.")
								.setPositiveButton("Ok", null).show();

			    		
			    		err.printStackTrace();
			    		
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
			      Log.d("MyApp", "User logged in through Facebook! id:" + user.getObjectId() );
			      
			      //Ver Home
			      
			      	if (ParseUser.getCurrentUser().getObjectId() != null) {
			      		showHome();
			      	}else{
			      		
			      	}
//			      showHome();
			    }
			  }
			});
		
	}

	protected void showPerfil() {

		Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
		intent.putExtra(PerfilActivity.USER_NEW, true);
		startActivity(intent);
		finish();
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
							
							if (ParseUser.getCurrentUser().getObjectId() != null) {
								showHome();
							}

//							showHome();

						} else {
							e.printStackTrace();
						}
					}

				});

	}

	// / Mostrar el Home
	protected void showHome() {
		
		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

		startActivity(intent);
		finish();
	}
}
