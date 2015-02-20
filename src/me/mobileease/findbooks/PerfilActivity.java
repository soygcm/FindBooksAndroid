package me.mobileease.findbooks;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class PerfilActivity extends ActionBarActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfil);
		
		 Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	        if (toolbar != null) {  		
	        		setSupportActionBar(toolbar);
	        }
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	}

}


