package me.mobileease.findbooks.adapter;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import me.mobileease.findbooks.BookActivity;
import me.mobileease.findbooks.FindBooks;
import me.mobileease.findbooks.R;
import me.mobileease.findbooks.TransactionActivity;
import me.mobileease.findbooks.TransactionsActivity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class TransactionAdapter extends ArrayAdapter<ParseObject> implements OnItemClickListener {

	private LayoutInflater inflater;
	private List<ParseObject> transactions;
	private ParseUser user;
	private Context c;
	private boolean showBook;
	private boolean showOffer;

	public TransactionAdapter(Context context, List<ParseObject> objects, boolean showBook, boolean showOffer) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		c = context;
		transactions = objects;
		user = ParseUser.getCurrentUser();
		this.showBook = showBook;
		this.showOffer = showOffer;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		// reusar view
		if (convertView == null) {
			view = inflater
					.inflate(R.layout.adapter_transaction, parent, false);

			// configurar el view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtDescription = (TextView) view
					.findViewById(R.id.txtDescription);
			viewHolder.imgBook = (ImageView) view.findViewById(R.id.imgBook);
			viewHolder.imgArrow = (ImageView) view.findViewById(R.id.imgArrow);

			view.setTag(viewHolder);
		}

		// rellenar de datos
		ParseObject transaction = transactions.get(position);
		boolean accepted = transaction.getBoolean("accepted");
		ViewHolder holder = (ViewHolder) view.getTag();

		ParseObject bookOffer = transaction.getParseObject("bookOffer");
		ParseUser userOffer = bookOffer.getParseUser("user");
		ParseObject book = bookOffer.getParseObject("book");
		ParseObject bookWant = transaction.getParseObject("bookWant");
		ParseUser userWant = bookWant.getParseUser("user");

		StringBuilder messageTransaction = new StringBuilder();

		if (user.getObjectId().equals(userWant.getObjectId())) {

			if (accepted) {
				messageTransaction.append("<b>Tú</b> ");
				messageTransaction.append("y ");
				messageTransaction.append("<b>"
						+ userOffer.getString("username") + "</b>, ");
				messageTransaction.append("están en contacto por ");
				if (!showBook) {					
					messageTransaction.append("este libro ");
				}
			} else {
				messageTransaction.append("<b>Tú</b> ");
				messageTransaction.append("deseas adquirir el libro de ");
				messageTransaction.append("<b>"
						+ userOffer.getString("username") + "</b>");
				if (showBook) {					
					messageTransaction.append(", ");
				}
			}

			holder.imgArrow.setImageResource(R.drawable.ic_detalles_buscar);
		} else {

			if (accepted) {
				messageTransaction.append("<b>Tú</b> ");
				messageTransaction.append("y ");
				messageTransaction.append("<b>"
						+ userWant.getString("username") + "</b>, ");
				messageTransaction.append("están en contacto por tu libro ");
			} else {
				messageTransaction.append("<b>"
						+ userWant.getString("username") + "</b>, ");
				messageTransaction.append("desea adquirir tu libro ");
			}
			holder.imgArrow.setImageResource(R.drawable.ic_detalles_agregar);
		}
		if (showBook) {			
			messageTransaction.append("<b>" + book.getString("title") + "</b> ");
		}
		if (showOffer){
			messageTransaction.append("<font color=\"#00BA16\">"
					+ precio(bookOffer) + "</font> ");			
		}

		holder.txtDescription.setText(Html.fromHtml(messageTransaction
				.toString()));

		JSONObject imageLinks = book.getJSONObject("imageLinks");

		if (imageLinks != null) {
			try {
				Ion.with(holder.imgBook).load(imageLinks.getString("thumbnail"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			holder.imgBook.setImageResource(android.R.color.transparent);
		}

		return view;
	}

	private String precio(ParseObject bookOffer) {
		Double price = bookOffer.getDouble("price");
		String offerCurrency = bookOffer.getString("currency");

		if (price != 0) {
			NumberFormat format = NumberFormat.getCurrencyInstance();
			if (offerCurrency != null) {
				Currency currency = Currency.getInstance(offerCurrency);
				format.setCurrency(currency);
			}
			return format.format(price);
		} else {
			return "gratis";
		}
	}

	static class ViewHolder {
		public TextView txtDescription;
		public TextView price;
		public ImageView imgBook;
		public ImageView imgArrow;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		if (c instanceof BookActivity) {			
			showTransaction(position-1);
		}else{
			showTransaction(position);
		}

	}

	/**
	 * BookOffer: Titulo, Subtitulo, autores, foto MyBook: FotoOferta, Estado,
	 * Descripción, precio, moneda, binding User-Other: Usuario, telefono, etc.
	 */
	private void showTransaction(int position) {

		Intent intent = new Intent(c,
				TransactionActivity.class);

		ParseObject transaction = getItem(position);
		boolean accepted = transaction.getBoolean("accepted");
		ParseObject bookOffer = transaction.getParseObject("bookOffer");
		ParseObject bookWant = transaction.getParseObject("bookWant");
		ParseObject book = bookOffer.getParseObject("book");
		ParseUser userOffer = bookOffer.getParseUser("user");
		ParseUser userWant = bookWant.getParseUser("user");
		String transactionId = transaction.getObjectId();
		String title = book.getString("title");
		String subtitle = book.getString("subtitle");
		
		//offer
		String offerCondition = bookOffer.getString("condition");
		String offerBinding = bookOffer.getString("bookbinding");
		String offerComment = bookOffer.getString("comment");
		String offerCurrency = bookOffer.getString("currency");
		Double price = bookOffer.getDouble("price");
		String offerPrice = "";
		if (price != 0) {
			NumberFormat format = NumberFormat.getCurrencyInstance();
			if (offerCurrency != null) {
				Currency currency = Currency.getInstance(offerCurrency);
				format.setCurrency(currency);
			}
			offerPrice = format.format(price);
		} else {
			offerPrice = "gratis";
		}
		
		Log.d(FindBooks.TAG, "price: "+offerPrice);

		String userPhone;
		String userName;
		String userMail;

		boolean offering = user.getObjectId().equals(userOffer.getObjectId());
		if (offering) {
			userName = userWant.getString("username");
			userPhone = userWant.getString("phone");
			userMail = userWant.getString("email");
		} else {
			userName = userOffer.getString("username");
			userPhone = userOffer.getString("phone");
			userMail = userOffer.getString("email");
		}

		List<String> authorsList = book.getList("authors");
		String authors = TextUtils.join(", ", authorsList);
		JSONObject image = book.getJSONObject("imageLinks");
		String imageLink = null;

		if (image != null) {
			try {
				imageLink = image.getString("thumbnail");
				// imageLink = imageLink.replaceAll("zoom=[^&]+","zoom=" + 4);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		intent.putExtra(BookActivity.BOOK_TITLE, title);
		intent.putExtra(BookActivity.BOOK_SUBTITLE, subtitle);
		intent.putExtra(BookActivity.BOOK_AUTHORS, authors);
		intent.putExtra(BookActivity.BOOK_IMAGE, imageLink);
		intent.putExtra(TransactionActivity.TRANSACTION_ID, transactionId);
		
		intent.putExtra(TransactionActivity.OFFER_CONDITION, offerCondition);
		intent.putExtra(TransactionActivity.OFFER_PRICE, offerPrice);
		intent.putExtra(TransactionActivity.OFFER_COMMENT, offerComment);
		intent.putExtra(TransactionActivity.OFFER_BINDING, offerBinding);
		
		intent.putExtra(TransactionActivity.USER_NAME, userName);
		intent.putExtra(TransactionActivity.USER_PHONE, userPhone);
		intent.putExtra(TransactionActivity.USER_MAIL, userMail);
		intent.putExtra(TransactionActivity.OFFERING, offering);
		intent.putExtra(TransactionActivity.ACCEPTED, accepted);

		//
		c.startActivity(intent);
	}
}
