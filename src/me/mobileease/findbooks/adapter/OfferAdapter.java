package me.mobileease.findbooks.adapter;

import java.util.List;

import me.mobileease.findbooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;

/// Es posible que la mejor opci—n sea ArrayAdapter

public class OfferAdapter extends BaseAdapter {
    private Context mContext;
    private List<ParseObject> offerList;
	private LayoutInflater inflater;

    public OfferAdapter(Context c, List<ParseObject> list) {
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
        return offerList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    /// Crear una nueva vista con imagen y titulo
    public View getView(int position, View convertView, ViewGroup parent) {
    		View view = convertView;
    		// reuse views
        if (convertView == null) {
          view = inflater.inflate(R.layout.adapter_offer, null);
          // configure view holder
          ViewHolder viewHolder = new ViewHolder();
          viewHolder.title = (TextView) view.findViewById(R.id.title);
          viewHolder.number = (TextView) view.findViewById(R.id.numberNotifications);
          viewHolder.image = (ImageView) view.findViewById(R.id.imageBook);
          view.setTag(viewHolder);
        }

        // fill data
        ParseObject offer = offerList.get(position);
        ParseObject book = offer.getParseObject("book");
        JSONObject imageLinks = book.getJSONObject("imageLinks");
        ViewHolder holder = (ViewHolder) view.getTag();
        
        holder.title.setText( book.getString("title") );
        holder.number.setText( offer.getNumber("price").toString() );
        
        if (imageLinks != null) {
	        	try {
				Ion.with(holder.image).load( imageLinks.getString("thumbnail") );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

        return view;
    }

    
}
