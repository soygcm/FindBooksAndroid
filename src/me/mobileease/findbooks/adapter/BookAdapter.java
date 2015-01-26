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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BookAdapter extends ArrayAdapter<ParseObject> {

	static class ViewHolder {
        public TextView number;
        public TextView title;
        public ImageView image;
        public ImageView imgArrow;
    }

	private LayoutInflater inflater;
	private List<ParseObject> books;
	private boolean searchFind;
	
	public BookAdapter(Context context, List<ParseObject> objects, boolean searchFind) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		books = objects;
		this.searchFind = searchFind;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		// reusar view
	    if (convertView == null ) {
			  view = inflater.inflate(R.layout.adapter_book, null);
			  
			  // configurar el view holder
			  ViewHolder viewHolder = new ViewHolder();
			  viewHolder.title = (TextView) view.findViewById(R.id.txtTitle);
			  viewHolder.image = (ImageView) view.findViewById(R.id.imgBook);
			  viewHolder.imgArrow = (ImageView) view.findViewById(R.id.imgArrow);
			  view.setTag(viewHolder);
	    }

	    // rellenar de datos
	    ParseObject book = books.get(position);
	    ViewHolder holder = (ViewHolder) view.getTag();
	
	    String title = book.getString("title");
        JSONObject imageLinks = book.getJSONObject("imageLinks");

	    holder.title.setText( title );
	    
	    if(searchFind){
	    		holder.imgArrow.setImageResource(R.drawable.ic_detalles_buscar);
	    }else{
	    		holder.imgArrow.setImageResource(R.drawable.ic_detalles_agregar);
	    }
	    
	    if (imageLinks != null) {
	        	try {
				Ion.with(holder.image).load( imageLinks.getString("thumbnail") );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			holder.image.setImageResource(android.R.color.transparent);
		}
	    
	    return view;
	}
		
}	
