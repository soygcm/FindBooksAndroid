package me.mobileease.findbooks.fragment;


import java.math.BigDecimal;

import me.mobileease.findbooks.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.ParseObject;

public class FormOfferFragment extends Fragment {

	public static final String FORM = "form";
	private EditText txtPrice;
	private ParseObject offer;
	
	public FormOfferFragment(ParseObject offer) {
		this.offer = offer;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
				
		Bundle args = getArguments();
		Integer form = args.getInt(FORM);
		View rootView = null;
		
		if (form == 0) {
			
			rootView = inflater.inflate(R.layout.fragment_sell, container, false);
						
			
			txtPrice = (EditText) rootView.findViewById(R.id.price);
			txtPrice.addTextChangedListener(new TextWatcher() {

			    public void onTextChanged(CharSequence s, int start, int before,
			            int count) {
			        if(!s.equals("") ){
						BigDecimal price = new BigDecimal(txtPrice.getText().toString());
			        		offer.put("price", price);
			        }
			    }
			    public void beforeTextChanged(CharSequence s, int start, int count,
			            int after) { }
			    public void afterTextChanged(Editable s) {  }
			});
		
		}else if (form == 1) {
			rootView = inflater.inflate(R.layout.fragment_gift, container, false);
		}

        return rootView;
		
	}
	
	/*Spinner spinner = (Spinner) rootView.findViewById(R.id.currency);
	// Create an ArrayAdapter using the string array and a default spinner layout
				
	ArrayList<CharSequence> currencyNames = new ArrayList<CharSequence>();
	
	String[] currencyCodeArray = getResources().getStringArray(R.array.currency_codes_array);
	
	List<String> currencyCodeList = Arrays.asList(currencyCodeArray);
	
	for ( CurrencyCode code : CurrencyCode.values() ) {
        
		Currency currency = code.getCurrency();
		Iterator<CountryCode> i = code.getCountryList().iterator();				
		
		if (currency != null && i.hasNext() ){	
			
			CountryCode country = i.next();
			
			Log.d("FB", code + ", "+  country.toLocale().toString() );
			
			currencyNames.add(code.getName() +" ("+ currency.getSymbol( country.toLocale() ) + ") " );
		}
	
	}
	
	for (String code : currencyCodeList) {
		
		try {
			Currency currency = Currency.getInstance(code);	
			currencyNames.add( currency.getSymbol() );
		} catch (IllegalArgumentException e) {
			
//			Log.d("FB", e.getMessage() );

		}
		
	}
	
	ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, currencyNames);
	
			
//			ArrayAdapter.createFromResource(getActivity(), R.array.currency_codes_array, android.R.layout.simple_spinner_item);
	// Specify the layout to use when the list of choices appears
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	// Apply the adapter to the spinner
	spinner.setAdapter(adapter);*/
}
