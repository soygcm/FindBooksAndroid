package me.mobileease.findbooks;

import me.mobileease.findbooks.helpers.FindBooksConfig;
import me.mobileease.findbooks.model.User;
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class FindBooks extends Application {

	public static final String TAG = "FB";
	
	public interface FirstConfigCallback {
		void done(Exception e);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, "C4zgFUST9RGWSJ5scVpyB5G4co2gcMUpNPg0QpaI",
				"ZLxGCOCWUpKAJP9mnX1Dl54UXKD7EvSqNDmP42Er");
		ParseFacebookUtils.initialize(getResources().getString(R.string.facebook_app_id));

		// / Que es todo esto??
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
	}

	public static void firstConfig(final FirstConfigCallback callback) {		
		
		FindBooksConfig.refreshConfig(new FirstConfigCallback() {
			@Override
			public void done(Exception e) {
				if(e == null){
					linkUserInstallation(new FirstConfigCallback() {
						@Override
						public void done(Exception e) {
							callback.done(e);
						}
					});
				}else{
					callback.done(e);
				}
			}
		});
		
	}

	private static void linkUserInstallation(final FirstConfigCallback callback) {

		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put("user", User.getCurrentUser().getParseUser());
		installation.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				callback.done(e);
			}
		});
		
	}
}
