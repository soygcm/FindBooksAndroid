package me.mobileease.findbooks.adapter;

import java.util.List;

import me.mobileease.findbooks.R;

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
	
	public BookOfferAdapter(Context context, List<ParseObject> objects) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		offers = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		ParseObject offer = offers.get(position);
		// reusar view
	    if (convertView == null ) {
			  view = inflater.inflate(R.layout.adapter_book_offer, null);
			  
			  // configurar el view holder
			  ViewHolder viewHolder = new ViewHolder(offer);
			  viewHolder.title = (TextView) view.findViewById(R.id.title);
			  viewHolder.btnWant = (Button) view.findViewById(R.id.btnWant);
			  
			  view.setTag(viewHolder);
	    }

	    // rellenar de datos
	    ParseObject book = offer.getParseObject("book");
	    ViewHolder holder = (ViewHolder) view.getTag();
	    
	    String title = book.getString("title");
	    
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
        
		public ViewHolder(ParseObject offer) {
			this.offer = offer;
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
						
			if(id == R.id.btnWant){
				
				newTransaction(offer);
				
			}
		}

		private void newTransaction(ParseObject offer) {
			
			
			ParseObject transaction = new ParseObject("Transaction");
			transaction.put("offer", offer);
			transaction.put("userRequest", ParseUser.getCurrentUser());
			transaction.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					if(e== null){
						btnWant.setBackgroundColor(Color.GREEN);
					}
				}
			});
			
		}
        
    }
		
}	
