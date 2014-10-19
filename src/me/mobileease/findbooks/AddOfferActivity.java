package me.mobileease.findbooks;

import me.mobileease.findbooks.fragment.FormOfferFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AddOfferActivity extends ActionBarActivity {

	private FormOfferPagerAdapter formOfferPagerAdapter;
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_offer);
		
		formOfferPagerAdapter = new FormOfferPagerAdapter(getSupportFragmentManager());

        // Set up action bar.
        final ActionBar actionBar = getSupportActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(formOfferPagerAdapter);
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class FormOfferPagerAdapter extends FragmentStatePagerAdapter {

        public FormOfferPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new FormOfferFragment();
            Bundle args = new Bundle();
            args.putInt(FormOfferFragment.FORM, i); // Our object is just an integer :-P
            fragment.setArguments(args);
            
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "OBJECT " + (position + 1);
//        }
    }

}
