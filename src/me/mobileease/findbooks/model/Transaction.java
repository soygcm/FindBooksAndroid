package me.mobileease.findbooks.model;

import java.text.NumberFormat;
import java.util.Currency;

import me.mobileease.findbooks.FindBooks;
import me.mobileease.findbooks.R;
import me.mobileease.findbooks.TransactionActivity;

import android.content.Intent;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class Transaction extends MyParseObject {

	private ParseObject transaction;
	private boolean endedWant;
	private boolean endedOffer;
	private ParseObject bookOffer;
	private ParseUser userOffer;
	private ParseObject book;
	private ParseObject bookWant;
	private ParseUser userWant;
	private ParseUser user;
	private boolean accepted;
	private double price;
	private boolean offering;
	private String transactionId;
	private String userName;

	public Transaction(ParseObject transaction) {
		
		this.transaction = transaction;
		
		user = ParseUser.getCurrentUser();
		
		accepted = transaction.getBoolean("accepted");
		
		endedWant = transaction.getBoolean("endedWant");
		endedOffer = transaction.getBoolean("endedOffer");
		
		bookOffer = transaction.getParseObject("bookOffer");
		
		userOffer = bookOffer.getParseUser("user");
		
		book = bookOffer.getParseObject("book");
		bookWant = transaction.getParseObject("bookWant");
		userWant = bookWant.getParseUser("user");
		
		price = bookOffer.getDouble("price");

		offering = user.getObjectId().equals(userOffer.getObjectId());
		if (offering) {
			userName = userWant.getString("nickname");
		} else {
			userName = userOffer.getString("nickname");
		}
	}

	public Transaction(Intent intent) {

		offering = intent.getBooleanExtra(TransactionActivity.OFFERING, false);
		accepted = intent.getBooleanExtra(TransactionActivity.ACCEPTED, false);
		userName = intent.getStringExtra(TransactionActivity.USER_NAME);
		Log.d(FindBooks.TAG, "username: "+ userName);
		transactionId = intent.getStringExtra(TransactionActivity.TRANSACTION_ID);
		
		endedWant = intent.getBooleanExtra(TransactionActivity.TRANSACTION_END_WANT, false);
		endedOffer = intent.getBooleanExtra(TransactionActivity.TRANSACTION_END_OFFER, false);
		
	}

	public String getMessage(boolean showBook, boolean showOffer) {
		StringBuilder messageTransaction = new StringBuilder();
		
		if(endedWant || endedOffer){
			
			messageTransaction.append("<b>"
					+ userName + "</b>, ");
			messageTransaction.append("finalizó esta transacción");
			if (showBook) {					
				messageTransaction.append(", ");
			}else{
				messageTransaction.append(" ");
			}
		}else{
			if (!offering) {
	
				if (accepted) {
					messageTransaction.append("<b>Tú</b> ");
					messageTransaction.append("y ");
					messageTransaction.append("<b>"
							+ userName + "</b>, ");
					messageTransaction.append("están en contacto por ");
					if (!showBook) {					
						messageTransaction.append("este libro ");
					}
				} else {
					messageTransaction.append("<b>Tú</b> ");
					messageTransaction.append("deseas adquirir el libro de ");
					messageTransaction.append("<b>"
							+ userName + "</b>");
					if (showBook) {					
						messageTransaction.append(", ");
					}
				}
			} else {
	
				if (accepted) {
					messageTransaction.append("<b>Tú</b> ");
					messageTransaction.append("y ");
					messageTransaction.append("<b>"
							+ userName + "</b>, ");
					messageTransaction.append("están en contacto por tu libro ");
				} else {
					messageTransaction.append("<b>"
							+ userName + "</b>, ");
					messageTransaction.append("desea adquirir tu libro ");
				}
			}
		}
		
		if (showBook) {			
			messageTransaction.append("<b>" + book.getString("title") + "</b> ");
		}
		if (showOffer){
			messageTransaction.append("<font color=\"#00BA16\">"
					+ precio() + "</font> ");			
		}

		return messageTransaction.toString();
	}
	
	private String precio() {
		MyBook myBook = new MyBook(bookOffer);
		return myBook.getPriceFormated();
	}

}
