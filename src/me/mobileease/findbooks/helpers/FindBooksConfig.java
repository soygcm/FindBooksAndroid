package me.mobileease.findbooks.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;

import com.parse.ParseConfig;

public class FindBooksConfig {
	
	private ParseConfig config;
	
	
	
	public FindBooksConfig() {
		super();
		this.config = getCurrentConfig();
	}
	
	/**
	 * siempre al principio del app??
	 * cada cierto tiempo?? cada cuanto??
	 * @return
	 */
	private ParseConfig getCurrentConfig() {
		return ParseConfig.getCurrentConfig();
	}


	
	private String codeToName(JSONArray jsonList, String codeSearch) {

		String codeReturn = "";

		List<String> listName = JSONArrayToList(jsonList, "name");
		List<String> listCode = JSONArrayToList(jsonList, "code");

		for (String name : listName) {
			for (String code : listCode) {

				if (code.equals(codeSearch)) {
					codeReturn = name;
				}

			}
		}

		return codeReturn;
	}

	private List<String> JSONArrayToList(JSONArray jsonArray, String key) {
		List<String> returnList = new ArrayList<String>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				returnList.add(jsonArray.getJSONObject(i).getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return returnList;
	}

	/**
	 * Hacer una lista de EndOption, segun el endCodeParent y el endOption.
	 * Todo esto localizado
	 * @param endOption
	 * @param endCode
	 * @return
	 */
	public List<EndOption> getTransactionEndedOptionList(String endOption) {
		
		List<EndOption> list = new ArrayList<EndOption>();
		JSONObject transactionEnded = config.getJSONObject("TransactionEnded");
		JSONArray endOptionArray = null;
		try {
			endOptionArray = transactionEnded.getJSONArray(endOption);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(endOptionArray != null){
			
			//fill list
			
			for (int i = 0; i < endOptionArray.length(); i++) {
				
				EndOption option;
				try {
					option = new EndOption(endOptionArray.getJSONObject(i));
					list.add(option);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		return list;
	}
	
	public class EndOption {
		private String code = null;
		private boolean success = false;
		private JSONObject description;

		public EndOption(JSONObject jsonObject) {
			
			try {
				this.code = jsonObject.getString("code");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				this.success = jsonObject.getBoolean("success");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				this.description = jsonObject.getJSONObject("description");
			} catch (JSONException e) {
				e.printStackTrace();
			}
						
		}

		public String getCode() {
			return code;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getLocalizedDescription() {
			String returnDescription = null;
			
			Locale locale = Locale.getDefault();
			String language = locale.getLanguage();
			String country = locale.getCountry();
			
			String langCountry = language+"_"+country;
			
			try {
				returnDescription = description.getString(langCountry);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (returnDescription != null){
				return returnDescription;
			}
			
			try {
				returnDescription = description.getString(language);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (returnDescription != null){
				return returnDescription;
			}
			
			try {
				JSONArray arrayDescription = description.names();
				returnDescription = description.getString( arrayDescription.getString(0) );
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (returnDescription != null){
				return returnDescription;
			}else{
				return "";
			}

		}

		public boolean isTitle(){
			return code==null;
		}
		
		
	}

	public String getBindingLocalized(String offerBinding) {
		JSONArray mBookBookbinding = config.getJSONArray("MyBookBookbinding");
		return codeToName(mBookBookbinding, offerBinding);
	}

	public String getConditionLocalized(String offerCondition) {
		JSONArray mBookCondition = config.getJSONArray("MyBookCondition");
		return codeToName(mBookCondition, offerCondition);
	}

}
