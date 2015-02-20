package me.mobileease.findbooks.adapter;

import java.text.NumberFormat;
import java.util.List;

import me.mobileease.findbooks.FindBooks;
import me.mobileease.findbooks.R;
import me.mobileease.findbooks.helpers.FindBooksConfig.EndOption;

import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.ion.Ion;
import com.parse.ParseObject;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EndOptionAdapter extends ArrayAdapter<EndOption> {

	static class ViewHolder {
		public TextView description;
		public ImageView image;
		public TextView title;
		public View optionView;
	}

	private LayoutInflater inflater;
	private List<EndOption> endOptions;

	public EndOptionAdapter(Context context, List<EndOption> objects) {
		super(context, -1, objects);
		inflater = LayoutInflater.from(context);
		endOptions = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		// reusar view
		if (convertView == null) {
			view = inflater.inflate(R.layout.adapter_end_transaction, parent, false);
			
			// configurar el view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.description = (TextView) view.findViewById(R.id.txtDescription);
			viewHolder.title = (TextView) view.findViewById(R.id.txtTitle);
			viewHolder.optionView = (View) view.findViewById(R.id.optionView);
			
			viewHolder.image = (ImageView) view.findViewById(R.id.imgBook);
			
			view.setTag(viewHolder);
		}

		// rellenar de datos
		ViewHolder holder = (ViewHolder) view.getTag();
		EndOption endOption = endOptions.get(position);
		
		String description = endOption.getLocalizedDescription();
		boolean success = endOption.isSuccess();
		boolean isTitle = endOption.isTitle();
		
		if(isTitle){
			holder.title.setVisibility(View.VISIBLE);
			holder.optionView.setVisibility(View.GONE);
		}else{
			holder.title.setVisibility(View.GONE);
			holder.optionView.setVisibility(View.VISIBLE);
		}
		
		holder.title.setText(description);
		holder.description.setText(description);

		return view;
	}

}
