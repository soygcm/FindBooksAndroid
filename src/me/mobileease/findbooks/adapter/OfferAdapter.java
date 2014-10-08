package me.mobileease.findbooks.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
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

public class OfferAdapter extends BaseAdapter {
    private Context mContext;
    private List<ParseObject> offerList;

    public OfferAdapter(Context c, List<ParseObject> list) {
        mContext = c;
        offerList = list;
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
        ImageView imageView;
        LinearLayout layout;
        TextView text;
        text = new TextView(mContext);
		imageView = new ImageView(mContext);

        if (convertView == null) {  // if it's not recycled, initialize some attributes
        		
        		layout = new LinearLayout(mContext);
        		layout.setLayoutParams(new GridView.LayoutParams(85, 200));
        		layout.setOrientation(LinearLayout.VERTICAL);
        	
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(Color.RED);
            imageView.setPadding(8, 8, 8, 8);
            
            text.setGravity(Gravity.CENTER);
//            text.setId(1);
            text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 40) );
                        
        } else {
            layout = (LinearLayout) convertView;
        }

        ParseObject offer = offerList.get(position);
        ParseObject book = offer.getParseObject("book");
        
        	text.setText(position + ". " + book.getString("title") );
        	
        	JSONObject imageLinks = book.getJSONObject("imageLinks");
        	
        if (imageLinks != null) {
	        	try {
				Ion.with(imageView).load( imageLinks.getString("thumbnail") );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        	
        	
        	layout.removeAllViews();
        	layout.addView(imageView);
        layout.addView(text);
       
        
        return layout;
    }

    
}
