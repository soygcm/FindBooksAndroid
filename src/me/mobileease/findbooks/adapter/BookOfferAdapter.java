package me.mobileease.findbooks.adapter;

import java.util.List;

import me.mobileease.findbooks.R;
import me.mobileease.findbooks.adapter.OfferAdapter.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookOfferAdapter extends ArrayAdapter<ParseObject> {

	static class ViewHolder {
        public TextView number;
        public TextView title;
        public ImageView image;
    }

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
		
		// reusar view
	    if (convertView == null ) {
			  view = inflater.inflate(R.layout.adapter_book_offer, null);
			  
			  // configurar el view holder
			  ViewHolder viewHolder = new ViewHolder();
			  viewHolder.title = (TextView) view.findViewById(R.id.title);
			  view.setTag(viewHolder);
	    }

	    // rellenar de datos
	    ParseObject offer = offers.get(position);
	    ParseObject book = offer.getParseObject("book");
	    ViewHolder holder = (ViewHolder) view.getTag();
	
	    String title = book.getString("title");
	    
	    holder.title.setText( title );
	    
	    return view;
	}
		
}	
