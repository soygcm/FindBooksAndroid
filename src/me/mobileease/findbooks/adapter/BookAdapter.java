package me.mobileease.findbooks.adapter;

import java.text.NumberFormat;
import java.util.List;

import me.mobileease.findbooks.R;
import me.mobileease.findbooks.adapter.MyBookAdapter.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
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
		public TextView authors;
		public ImageView image;
		public ImageView imgArrow;
		public TextView offerCount;
	}

	private LayoutInflater inflater;
	private List<ParseObject> books;
	private boolean searchFind;
	private Context c;

	public BookAdapter(Context context, List<ParseObject> objects,
			boolean searchFind) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		books = objects;
		this.c = context;
		this.searchFind = searchFind;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		// reusar view
		if (convertView == null) {
			view = inflater.inflate(R.layout.adapter_book, parent, false);

			// configurar el view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) view.findViewById(R.id.txtTitle);
			viewHolder.image = (ImageView) view.findViewById(R.id.imgBook);
			viewHolder.imgArrow = (ImageView) view.findViewById(R.id.imgArrow);
			viewHolder.authors = (TextView) view.findViewById(R.id.txtAuthors);
			viewHolder.offerCount = (TextView) view.findViewById(R.id.txtOfferCount);
			
			
			view.setTag(viewHolder);
		}

		// rellenar de datos
		ParseObject book = books.get(position);
		ViewHolder holder = (ViewHolder) view.getTag();

		String title = book.getString("title");
		String subtitle = book.getString("subtitle");

		JSONObject imageLinks = book.getJSONObject("imageLinks");
		List<String> authorsList = book.getList("authors");

		if (subtitle == null) {
			holder.title.setText(title);
		} else {
			holder.title.setText(Html.fromHtml(title + ": <small>" + subtitle
					+ "</small>"));
		}

		if (authorsList != null) {
			String authors = TextUtils.join(", ", authorsList);
			holder.authors.setText(authors);
		} else {
			holder.authors.setVisibility(View.GONE);
		}

		int count = book.getInt("offersCount");
		int countWants = book.getInt("wantsCount");
		
		if (searchFind) {
			if (count == 0){
				holder.offerCount.setVisibility(View.GONE);
			}else{
				holder.offerCount.setVisibility(View.VISIBLE);
				holder.offerCount.setText(count+" ofertas");
			}
			holder.offerCount.setTextColor(c.getResources().getColor(
					R.color.ui_menta_busqueda));
			holder.imgArrow.setImageResource(R.drawable.ic_detalles_buscar);
		} else {
			if (countWants == 0){
				holder.offerCount.setVisibility(View.GONE);
			}else{
				holder.offerCount.setVisibility(View.VISIBLE);
				holder.offerCount.setText(countWants+" lo quieren");
			}
			holder.offerCount.setTextColor(c.getResources().getColor(
					R.color.ui_magenta_oferta));
			holder.imgArrow.setImageResource(R.drawable.ic_detalles_agregar);
		}

		if (imageLinks != null) {
			try {
				Ion.with(holder.image).load(imageLinks.getString("thumbnail"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			holder.image.setImageResource(android.R.color.transparent);
		}

		return view;
	}

}
