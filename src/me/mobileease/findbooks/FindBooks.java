package me.mobileease.findbooks;

import java.io.File;

import me.mobileease.findbooks.helpers.FindBooksConfig;
import me.mobileease.findbooks.model.User;
import android.app.Application;
import android.util.Log;

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
	
	public void clearApplicationData() {
	    File cache = getCacheDir();
	    File appDir = new File(cache.getParent());
	    if (appDir.exists()) {
	        String[] children = appDir.list();
	        for (String s : children) {
	            if (!s.equals("lib")) {
	                deleteDir(new File(appDir, s));
	                Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
	            }
	        }
	    }
	}
	
	public static boolean deleteDir(File dir) {
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    return dir.delete();
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
