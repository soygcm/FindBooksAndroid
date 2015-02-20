package me.mobileease.findbooks;

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
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends ActionBarActivity implements OnClickListener {
	private EditText passwordInput;
	private EditText usernameInput;
	private Button loginButton;
	private ProgressDialog progress;
	private Button signupButton;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (ParseUser.getCurrentUser().getObjectId() != null) {

			showHome();
		}

		findViewsAndSetListeners();

		ParseAnalytics.trackAppOpened(getIntent());
	}

	// / Obtener las vistas desde el layout y guardarlas en campos de la clase
	private void findViewsAndSetListeners() {
		usernameInput = (EditText) findViewById(R.id.login_username_input);
		passwordInput = (EditText) findViewById(R.id.login_password_input);
		loginButton = (Button) findViewById(R.id.parse_login_button);
		signupButton = (Button) findViewById(R.id.parse_signup_button);

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
		// EditText editText = (EditText) findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
}
