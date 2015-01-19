package me.mobileease.findbooks;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class FindBooks extends Application {

  protected static final String TAG = "FB";

@Override
  public void onCreate() {
    super.onCreate();

    // Add your initialization code here
    Parse.enableLocalDatastore(this);
    Parse.initialize(this, "C4zgFUST9RGWSJ5scVpyB5G4co2gcMUpNPg0QpaI", "ZLxGCOCWUpKAJP9mnX1Dl54UXKD7EvSqNDmP42Er");

    /// Que es todo esto??
    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    		// If you would like all objects to be private by default, remove this line.
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
