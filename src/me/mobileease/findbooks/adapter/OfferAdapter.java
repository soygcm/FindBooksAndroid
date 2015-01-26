package me.mobileease.findbooks.adapter;

import java.util.List;


import me.mobileease.findbooks.HomeActivity;
import me.mobileease.findbooks.R;
import me.mobileease.findbooks.SearchActivity;
import me.mobileease.findbooks.model.MyBook;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;

/// Es posible que la mejor opción sea ArrayAdapter
public class OfferAdapter extends ArrayAdapter<ParseObject> implements OnClickListener {
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
        return offerList.size()+1;
    }

    /// Crear una nueva vista con imagen y titulo
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    		View view;
    	
    		if(position == 0){
    			
    			view = inflater.inflate(R.layout.home_grid_button, parent, false);
    			
    			View buscar = (View) view.findViewById(R.id.buscar_libro);
    			View agregar = (View) view.findViewById(R.id.agregar_libro);
    			
    			buscar.setOnClickListener(this);
    			agregar.setOnClickListener(this);
  	        	          	        
    		}else{

	    		view = convertView;
	    		
	    		// verificar si ya tiene un viewHolder, o si toca crearlo
	    		boolean haveViewHolder = false;
	    		
	    		if(view != null){
	    			Object tag = view.getTag();
		    		if (tag != null){
		    			if(tag instanceof ViewHolder){
		    				haveViewHolder = true;
		    			}
		    		}
	    		}
	    		
	    		
	    		// reusar view
	        if (convertView == null || !haveViewHolder ) {
	          view = inflater.inflate(R.layout.adapter_offer, null);
	          // configurar el view holder
	          ViewHolder viewHolder = new ViewHolder();
	          viewHolder.title = (TextView) view.findViewById(R.id.title);
	          viewHolder.number = (TextView) view.findViewById(R.id.txtNumber);
	          viewHolder.image = (ImageView) view.findViewById(R.id.imageBook);
	          view.setTag(viewHolder);
	        }
	
	        // rellenar de datos
	        ParseObject offer = offerList.get(position-1);
	        ParseObject book = offer.getParseObject("book");
	        Number count = offer.getNumber("transactionCount");	
	        JSONObject imageLinks = null;
	        imageLinks = book.getJSONObject("imageLinks");
	        String title = book.getString("title");
	        String type = offer.getString(MyBook.TYPE);
	            
	        ViewHolder holder = (ViewHolder) view.getTag();
	        
	        holder.title.setText( title );
	        
	        if(count == null){
	        		holder.number.setVisibility(View.GONE);
	        }else{
	        		holder.number.setVisibility(View.VISIBLE);
	        		holder.number.setText( "" + count );
	        }
	        
	        if(type.equals(MyBook.OFFER)){
	        		holder.number.setBackgroundColor(mContext.getResources().getColor(R.color.ui_magenta_oferta));
	        }else if(type.equals(MyBook.WANT)){
        			holder.number.setBackgroundColor(mContext.getResources().getColor(R.color.ui_menta_busqueda));
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
	        
    		}

        return view;
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.agregar_libro){
			showSearch(false);
		}else if(id == R.id.buscar_libro){
			showSearch(true);
		}
		
	}
	
	protected void showSearch(boolean buscar) {

		Intent intent = new Intent(mContext, SearchActivity.class);
	    intent.putExtra(HomeActivity.SEARCH_FIND, buscar );
	    intent.putExtra(HomeActivity.SEARCH_ADD, !buscar );
	    mContext.startActivity(intent);
		
	}

    
}
