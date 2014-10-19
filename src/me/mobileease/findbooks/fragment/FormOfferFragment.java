package me.mobileease.findbooks.fragment;


import me.mobileease.findbooks.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FormOfferFragment extends Fragment {

	public static final String FORM = "form";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
				
		Bundle args = getArguments();
		Integer form = args.getInt(FORM);
		View rootView = null;
		
		if (form == 0) {
			rootView = inflater.inflate(R.layout.fragment_sell, container, false);
		}else if (form == 1) {
			rootView = inflater.inflate(R.layout.fragment_gift, container, false);
		}

        return rootView;
		
	}

}
