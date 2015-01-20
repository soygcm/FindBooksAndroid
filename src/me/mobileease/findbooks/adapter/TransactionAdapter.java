package me.mobileease.findbooks.adapter;

import java.util.List;

import me.mobileease.findbooks.R;

import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TransactionAdapter extends ArrayAdapter<ParseObject> {

	private LayoutInflater inflater;
	private List<ParseObject> transactions;

	public TransactionAdapter(Context context, List<ParseObject> objects) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		transactions = objects;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		// reusar view
	    if (convertView == null ) {
			  view = inflater.inflate(R.layout.adapter_transaction, parent, false);
			  
			  // configurar el view holder
			  ViewHolder viewHolder = new ViewHolder();
			  viewHolder.txtDescription = (TextView) view.findViewById(R.id.txtDescription);
			  
			  view.setTag(viewHolder);
	    }

	    // rellenar de datos
	    ParseObject transaction = transactions.get(position);
	    ViewHolder holder = (ViewHolder) view.getTag();
	    
//	    ParseObject bookOffer = transaction.getParseObject("bookOffer");
//	    ParseUser userOffer = bookOffer.getParseUser("user");
	    ParseObject bookWant = transaction.getParseObject("bookWant");
	    ParseUser userWant = bookWant.getParseUser("user");
	    
	    holder.txtDescription.setText(userWant.getString("username")+", Quiere el libro...");
	    
	    return view;
	}
	
	static class ViewHolder {
        public TextView txtDescription;
        public TextView price;
        public ImageView imgBook;
    }
	
}
