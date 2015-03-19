package me.mobileease.findbooks.model;

import java.text.NumberFormat;
import java.util.Currency;

import me.mobileease.findbooks.TransactionActivity;

import android.content.Intent;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class MyBook extends ParseObject {

	public static final String CLASS = "MyBook";
	public static final String OFFER = "OFFER";
	public static final String WANT = "WANT";
	public static final String TYPE = "type";
	private String offerCurrency;
	private double price;
	
	

	public MyBook(ParseObject bookOffer) {
//		ParseUser user = ParseUser.getCurrentUser();
		ParseUser user = bookOffer.getParseUser("user");
		offerCurrency = user.getString("currency");
		price = bookOffer.getDouble("price");
	}

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
	
	public double getPrice() {
		return price;
	}
	
}
