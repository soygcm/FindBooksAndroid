package me.mobileease.findbooks.adapter;

import java.util.List;

import me.mobileease.findbooks.R;
import me.mobileease.findbooks.model.MyBook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BookOfferAdapter extends ArrayAdapter<ParseObject> {

	private LayoutInflater inflater;
	private List<ParseObject> offers;
	private ParseObject bookWant;
	
	public BookOfferAdapter(Context context, List<ParseObject> objects, ParseObject bookWant) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		offers = objects;
		this.bookWant = bookWant;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		ParseObject offer = offers.get(position);
		// reusar view
	    if (convertView == null ) {
			  view = inflater.inflate(R.layout.adapter_book_offer, null);
			  
			  // configurar el view holder
			  ViewHolder viewHolder = new ViewHolder(offer, bookWant);
			  viewHolder.title = (TextView) view.findViewById(R.id.txtUsername);
			  viewHolder.btnWant = (Button) view.findViewById(R.id.btnWant);
			  
			  view.setTag(viewHolder);
	    }

	    // rellenar de datos
	    ParseUser user = offer.getParseUser("user");
	    ViewHolder holder = (ViewHolder) view.getTag();
	    
	    String title = user.getString("username");
	    
	    holder.title.setText( title );
		holder.btnWant.setOnClickListener(holder);

	    return view;
	}
	
	static class ViewHolder implements OnClickListener{
        public TextView number;
        public TextView title;
        public ImageView image;
        public Button btnWant;
		private ParseObject offer;
		private ParseObject bookWant;
        
		public ViewHolder(ParseObject offer, ParseObject bookWant) {
			this.offer = offer;
			this.bookWant = bookWant;
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
						
			if(id == R.id.btnWant){
				
				newWant();
				
			}
		}

		private void newWant() {
			
			ParseObject book = offer.getParseObject("book");
			
			if(bookWant == null){
				final ParseObject want = new ParseObject(MyBook.CLASS);
				want.put("book", book);
				want.put("type", "WANT");
				want.put("user", ParseUser.getCurrentUser() );
				want.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						if(e== null){
							
							bookWant = want;
							
							newTransaction(offer, want);
							
							
						}
					}
				});
			
			}else{
				newTransaction(offer, bookWant);

			}
			
		}

		protected void newTransaction(ParseObject bookOffer, ParseObject bookWant) {
			ParseObject transaction = new ParseObject("Transaction");
			transaction.put("bookOffer", bookOffer);
			transaction.put("bookWant", bookWant);
			transaction.put("user", ParseUser.getCurrentUser() );
			transaction.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					if(e==null){
						btnWant.setBackgroundColor(Color.GREEN);									
					}
				}
			});
		}
        
    }
		
}	
