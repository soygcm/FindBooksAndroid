package me.mobileease.findbooks.model;

import java.text.NumberFormat;
import java.util.Currency;

import me.mobileease.findbooks.TransactionActivity;

import android.content.Intent;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class MyBook extends MyParseObject {

	public static final String CLASS = "MyBook";
	public static final String OFFER = "OFFER";
	public static final String WANT = "WANT";
	public static final String TYPE = "type";
	private String offerCurrency;
	private double price;
	private String offerCity;
	
	
	/* para los resultados, home, transacciones */
	public MyBook(ParseObject myBook) {
//		ParseUser user = ParseUser.getCurrentUser();
		ParseUser user = myBook.getParseUser("user");
		offerCurrency = user.getString("currency");
		price = myBook.getDouble("price");
		offerCity = user.getString("address");
	}

	/*En teoria esta opción de crear un MyBook, es cuando es un OFFER desde el home*/
	public MyBook(Intent intent) {
		ParseUser user = ParseUser.getCurrentUser();
		offerCurrency = user.getString("currency");
		price = intent.getDoubleExtra(TransactionActivity.OFFER_PRICE, -1);
	}

	public String getPriceFormated() {
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
	
	public String getCityAddress(){
		return offerCity;
	}
	
	public double getPrice() {
		return price;
	}
	
}
