package me.mobileease.findbooks.adapter;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import me.mobileease.findbooks.R;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class TransactionAdapter extends ArrayAdapter<ParseObject> {

	private LayoutInflater inflater;
	private List<ParseObject> transactions;
	private ParseUser user;
	private Context c;

	public TransactionAdapter(Context context, List<ParseObject> objects) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		c = context;
		transactions = objects;
		user = ParseUser.getCurrentUser();
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
			  viewHolder.imgBook = (ImageView) view.findViewById(R.id.imgBook);
			  viewHolder.imgArrow = (ImageView) view.findViewById(R.id.imgArrow);

			  view.setTag(viewHolder);
	    }

	    // rellenar de datos
	    ParseObject transaction = transactions.get(position);
	    boolean accepted = transaction.getBoolean("acepted");
	    ViewHolder holder = (ViewHolder) view.getTag();
	    
	    ParseObject bookOffer = transaction.getParseObject("bookOffer");
	    ParseUser userOffer = bookOffer.getParseUser("user");
	    ParseObject book = bookOffer.getParseObject("book");
	    ParseObject bookWant = transaction.getParseObject("bookWant");
	    ParseUser userWant = bookWant.getParseUser("user");
	    
	    StringBuilder messageTransaction = new StringBuilder();
	    
	    if( user.getObjectId().equals(userWant.getObjectId()) ){
	    	
	    		if( accepted ){
	    			messageTransaction.append("<b>Tú</b> ");
	    			messageTransaction.append("y ");
	    			messageTransaction.append("<b>"+userOffer.getString("username")+"</b>, ");
	    			messageTransaction.append("están en contacto por ");
	    		}else{
	    			messageTransaction.append("<b>Tú</b> ");
	    			messageTransaction.append("deseas adquirir el libro de ");
	    			messageTransaction.append("<b>"+userOffer.getString("username")+"</b>, ");
	    		}
	    		
	    		holder.imgArrow.setImageResource(R.drawable.ic_detalles_buscar);
//	    		setImageDrawable(c.getResources().getDrawable(R.drawable.ic_detalles_buscar));
	    		
	    }else{
	    	
		    	if( accepted ){
		    		messageTransaction.append("<b>Tú</b> ");
	    			messageTransaction.append("y ");
	    			messageTransaction.append("<b>"+userWant.getString("username")+"</b>, ");
	    			messageTransaction.append("están en contacto por tu libro ");
	    		}else{
	    			messageTransaction.append("<b>"+userWant.getString("username")+"</b>, ");
	    			messageTransaction.append("desea adquirir tu libro ");
	    		}
	    		holder.imgArrow.setImageResource(R.drawable.ic_detalles_agregar);
//	    		holder.imgArrow.setImageDrawable(c.getResources().getDrawable(R.drawable.ic_detalles_agregar));
	    }
	    messageTransaction.append("<b>"+book.getString("title")+"</b> ");
	    messageTransaction.append("<font color=\"#00BA16\">"+precio(bookOffer)+"</font> ");

	    holder.txtDescription.setText(Html.fromHtml(messageTransaction.toString()));
	    
        JSONObject imageLinks = book.getJSONObject("imageLinks");
	    
	    if (imageLinks != null) {
	        	try {
				Ion.with(holder.imgBook).load( imageLinks.getString("thumbnail") );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			holder.imgBook.setImageResource(android.R.color.transparent);
		}
	    
	    return view;
	}

	private String precio(ParseObject bookOffer) {
		Double price = bookOffer.getDouble("price");
	    String offerCurrency = bookOffer.getString("currency");

		if( price != 0){
			NumberFormat format = NumberFormat.getCurrencyInstance();
			if(offerCurrency != null){				
				Currency currency = Currency.getInstance(offerCurrency);
				format.setCurrency(currency);
			}
			return format.format(price); 
		}else{
			return "gratis"; 
		}
	}

	static class ViewHolder {
        public TextView txtDescription;
        public TextView price;
        public ImageView imgBook;
        public ImageView imgArrow;

    }
	
}
