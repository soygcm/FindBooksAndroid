package me.mobileease.findbooks.adapter;

import java.util.List;


import me.mobileease.findbooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;

/// Es posible que la mejor opci�n sea ArrayAdapter

public class OfferAdapter extends ArrayAdapter<ParseObject> {
    private Context mContext;
    private List<ParseObject> offerList;
	private LayoutInflater inflater;

    public OfferAdapter(Context c, List<ParseObject> list) {
    		super(c, -1, list);
    		mContext = c;
        offerList = list;
        inflater = LayoutInflater.from(c);
    }
    
    static class ViewHolder {
        public TextView number;
        public TextView title;
        public ImageView image;
    }

    public int getCount() {
        return offerList.size()+2;
    }

//    public ParseObject getItem(int position) {
//        return null;
//    }

//    public long getItemId(int position) {
//        return 0;
//    }

    /// Crear una nueva vista con imagen y titulo
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    		View view;
    	
    		if(position == 0 || position == 1){
    			
    			view = inflater.inflate(R.layout.home_grid_button, null);
    			
    			TextView title = (TextView) view.findViewById(R.id.title);
  	        ImageView image = (ImageView) view.findViewById(R.id.imageBook);
    			
  	        if (position == 0) {
				title.setText("Busca un libro");
			}
			else if(position == 1){
				title.setText("Agrega un libro");
			}
  	          	        
    		}else{

	    		view = convertView;
	    		
	    		// verificar si ya tiene un viewHolder, o si toca crearlo
	    		boolean haveViewHolder = false;
	    		Object tag = view.getTag();
	    		if (tag != null){
	    			if(tag instanceof ViewHolder){
	    				haveViewHolder = true;
	    			}
	    		}
	    		
	    		// reusar view
	        if (convertView == null || !haveViewHolder ) {
	          view = inflater.inflate(R.layout.adapter_offer, null);
	          // configurar el view holder
	          ViewHolder viewHolder = new ViewHolder();
	          viewHolder.title = (TextView) view.findViewById(R.id.title);
	          viewHolder.number = (TextView) view.findViewById(R.id.numberNotifications);
	          viewHolder.image = (ImageView) view.findViewById(R.id.imageBook);
	          view.setTag(viewHolder);
	        }
	
	        // rellenar de datos
	        ParseObject offer = offerList.get(position-2);
	        ParseObject book = offer.getParseObject("book");
	        JSONObject imageLinks = null;
	        ViewHolder holder = (ViewHolder) view.getTag();
	        Number price = offer.getNumber("price");
	
	        imageLinks = book.getJSONObject("imageLinks");
	        String title = book.getString("title");
	            
	        holder.title.setText( title );
	        holder.number.setText( "" + price );
	 
	       
	        if (imageLinks != null) {
		        	try {
					Ion.with(holder.image).load( imageLinks.getString("thumbnail") );
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				holder.image.setImageResource(android.R.color.transparent);
			}
	        
    		}

        return view;
    }

    
}
