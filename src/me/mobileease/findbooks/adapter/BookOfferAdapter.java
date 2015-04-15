package me.mobileease.findbooks.adapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.json.JSONArray;

import me.mobileease.findbooks.FindBooks;
import me.mobileease.findbooks.R;
import me.mobileease.findbooks.TransactionActivity;
import me.mobileease.findbooks.model.MyBook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.parse.ParseACL;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BookOfferAdapter extends ArrayAdapter<ParseObject> {

	private LayoutInflater inflater;
	private List<ParseObject> offers;
	private ParseObject bookWant;
	private ParseConfig config;
	public List<ParseObject> transactions;
	private Context c;

	public BookOfferAdapter(Context context, List<ParseObject> objects,
			ParseObject bookWant, List<ParseObject> transactions) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		offers = objects;
		this.c = context;
		this.bookWant = bookWant;
		this.transactions = transactions;
		config = ParseConfig.getCurrentConfig();
		if(transactions != null){
			offersOnTransaction();
		}
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		ParseObject offer = offers.get(position);
		// reusar view
		if (convertView == null) {
			view = inflater.inflate(R.layout.adapter_book_offer, null);

			// configurar el view holder
			ViewHolder viewHolder = new ViewHolder(offer, bookWant, this);
			viewHolder.username = (TextView) view
					.findViewById(R.id.txtUsername);
			viewHolder.imgUser = (ImageView) view.findViewById(R.id.imgPerfil);
			viewHolder.loading = (ProgressBar) view.findViewById(R.id.loading);

			viewHolder.price = (TextView) view.findViewById(R.id.txtPrice);
			viewHolder.condition = (TextView) view
					.findViewById(R.id.txtCondition);
			viewHolder.btnWant = (Button) view.findViewById(R.id.btnWant);

			view.setTag(viewHolder);
		}

		// rellenar de datos
		
		
		
		ParseUser user = offer.getParseUser("user");
		ViewHolder holder = (ViewHolder) view.getTag();

		String title = user.getString("nickname");
		
		String facebookId = user.getString("facebookId");
		if(facebookId != null){
			String imageURL = "https://graph.facebook.com/"+facebookId+"/picture?type=square&width=50&height=50";
			Ion.with(holder.imgUser).load(imageURL);			
		}
		
		String offerCondition = offer.getString("condition");
		String offerBinding = offer.getString("bookbinding");
		String offerComment = offer.getString("comment");

		MyBook myBook = new MyBook(offer);
		String offerPrice = myBook.getPriceFormated();
		
		boolean onTransaction = offer.getBoolean("onTransaction");
		if(onTransaction){
			holder.btnWant.setEnabled(false);
			holder.btnWant.setText("EN TRANSACCIÓN");
		}
		
		if (holder.isLoading){
			
			holder.loading.setVisibility(View.GONE);
			holder.btnWant.setEnabled(true);
			holder.btnWant.setText("SOLICITANDO...");

		}

		holder.username.setText(title);
		holder.btnWant.setOnClickListener(holder);
		holder.price.setText(offerPrice);

		JSONArray mBookBookbinding = config.getJSONArray("MyBookBookbinding");
		JSONArray mBookCondition = config.getJSONArray("MyBookCondition");

		offerBinding = TransactionActivity.codeToName(mBookBookbinding,
				offerBinding);
		offerCondition = TransactionActivity.codeToName(mBookCondition,
				offerCondition);

		holder.condition.setText(Html.fromHtml(offerBinding + 
				", "+ offerCondition + 
				", " + offerComment + 
				" ("+ myBook.getCityAddress()+")"));

		return view;
	}
	
	protected void offersOnTransaction() {


		for(ParseObject offer : offers){
			
			if(transactions != null){
				
				for (ParseObject transaction : transactions) {
					
					ParseObject bookOffer = transaction.getParseObject("bookOffer");
					
					if(offer.getObjectId().equals(bookOffer.getObjectId())){
						
						offer.put("onTransaction", true);
						
					}
					
				}
			}
			
		}
		
		
		
	}

	static class ViewHolder implements OnClickListener {
		public TextView condition;
		public TextView username;
		public TextView price;
		public ImageView imgOffer;
		public ImageView imgUser;
		public Button btnWant;
		private ParseObject offer;
		private ParseObject bookWant;
		private BookOfferAdapter adapter;
		private boolean isLoading;
		private ProgressBar loading;

		public ViewHolder(ParseObject offer, ParseObject bookWant, BookOfferAdapter adapter) {
			this.offer = offer;
			this.bookWant = bookWant;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();

			if (id == R.id.btnWant) {
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE){
							newWant();
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(adapter.c);
				builder.setMessage("¿Deseas que tus datos de contacto, sean enviados al dueño del libro? ")
						.setPositiveButton("Si", dialogClickListener)
						.setNegativeButton("No", dialogClickListener).show();


			}
		}

		private void newWant() {

			ParseObject book = offer.getParseObject("book");
			
			loading.setVisibility(View.VISIBLE);
			btnWant.setEnabled(false);
			isLoading = true;
			btnWant.setText("SOLICITANDO...");
			
			if (bookWant == null) {
				final ParseObject want = new ParseObject(MyBook.CLASS);
				want.put("book", book);
				want.put("type", "WANT");
				want.put("user", ParseUser.getCurrentUser());
				want.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {

							bookWant = want;

							newTransaction(offer, want);

						}
					}
				});

			} else {
				newTransaction(offer, bookWant);

			}

		}

		protected void newTransaction(ParseObject bookOffer,
				ParseObject bookWant) {
			
			final ParseObject transaction = new ParseObject("Transaction");
			transaction.put("bookOffer", bookOffer);
			transaction.put("bookWant", bookWant);
			ParseACL transactionACL = new ParseACL();
			ParseUser userOffer = bookOffer.getParseUser("user"); 
			ParseUser userWant = bookWant.getParseUser("user");
			transactionACL.setReadAccess(userOffer, true);
			transactionACL.setWriteAccess(userOffer, true);
			transactionACL.setReadAccess(userWant, true);
			transactionACL.setWriteAccess(userWant, true);
			transaction.setACL(transactionACL);
			transaction.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						
						loading.setVisibility(View.GONE);
						btnWant.setEnabled(true);
						isLoading = false;
												
						adapter.addTransaction(transaction);
						adapter.offersOnTransaction();
						adapter.notifyDataSetChanged();
						
					} else {
						e.printStackTrace();
					}
				}
			});
		}

	}

	protected void addTransaction(ParseObject transaction) {
		if(transactions == null){
			transactions = new ArrayList<ParseObject>();
		}
		
		transactions.add(transaction);
	}

}
