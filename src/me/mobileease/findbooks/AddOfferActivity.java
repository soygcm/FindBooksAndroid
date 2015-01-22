package me.mobileease.findbooks;

import me.mobileease.findbooks.fragment.FormOfferFragment;
import me.mobileease.findbooks.model.MyBook;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddOfferActivity extends ActionBarActivity implements OnClickListener{

	private FormOfferPagerAdapter formOfferPagerAdapter;
	private ViewPager mViewPager;
	private ImageView mImageView;
	private Button takePhotoBtn;
	private Button btnSave;
	private String bookId;
	private ParseObject offer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_offer);
		
		offer = new ParseObject(MyBook.CLASS);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mImageView = (ImageView) findViewById(R.id.photo);
		takePhotoBtn = (Button) findViewById(R.id.take_photo);
		btnSave = (Button) findViewById(R.id.save);
		takePhotoBtn.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		
		Intent intent = getIntent();
		bookId = intent.getStringExtra(BookActivity.BOOK_ID);
		
        // Set up action bar.
//        final ActionBar actionBar = getSupportActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
//        actionBar.setDisplayHomeAsUpEnabled(true);

        formOfferPagerAdapter = new FormOfferPagerAdapter(getSupportFragmentManager(), offer);
        mViewPager.setAdapter(formOfferPagerAdapter);
    }
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.take_photo){
			dispatchTakePictureIntent();
		}
		if(id == R.id.save){
			saveOffer();
		}
	}
	
	private void saveOffer() {

		ParseObject book = ParseObject.createWithoutData("Book", bookId);

		offer.put("book", book );
		offer.put("type", "OFFER");
		offer.put("user", ParseUser.getCurrentUser());
		offer.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null){
					
					Log.d("FB", "Oferta Guardada");
					
					showHome();
					
				}else{
					
				}
			}
		});
		
	}

	protected void showHome() {
		Intent intent = new Intent(this, HomeActivity.class);
	    startActivity(intent);
	}

	static final int REQUEST_IMAGE_CAPTURE = 1;

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        mImageView.setImageBitmap(imageBitmap);
	    }
	}

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class FormOfferPagerAdapter extends FragmentStatePagerAdapter {

    		private ParseObject offer;

    		public FormOfferPagerAdapter(FragmentManager fm, ParseObject offer) {
    			super(fm);
    			this.offer = offer;
    		}

		@Override
        public Fragment getItem(int i) {
            Fragment fragment = new FormOfferFragment(offer);
            Bundle args = new Bundle();
            args.putInt(FormOfferFragment.FORM, i); // Our object is just an integer :-P
            fragment.setArguments(args);
            
            return fragment;
        }

        @Override
        public int getCount() {
            return 1+1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

}
