package me.mobileease.findbooks.model;

import com.parse.ParseUser;

public class User extends MyParseObject {

	private static final String NICKNAME = "nickname";
	public ParseUser user;

	public User(ParseUser user) {
		this.user = user;
	}
	
	public ParseUser getParseUser(){
		return user;
	}

	public boolean isValidLogin() {
		return user.getObjectId() != null;
	}

	public static User getCurrentUser() {
		return new User(ParseUser.getCurrentUser());
	}

	public CharSequence getNickname() {
		return user.getString(NICKNAME);
	}
	
	
	
}

