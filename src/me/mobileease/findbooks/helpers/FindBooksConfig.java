package me.mobileease.findbooks.helpers;

import java.lang.reflect.InvocationTargetException;
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


	
	private String codeToName(List<AddOfferOptions> list, String codeSearch) {
		for (AddOfferOptions addOfferOption : list) {
			if(addOfferOption.getCode().equals(codeSearch)){
				return addOfferOption.getName();
			}
		}

		return null;
	}

	/**
	 * Hacer una lista de EndOption, segun el endCodeParent y el endOption.
	 * Todo esto localizado
	 * @param endOption
	 * @param endCode
	 * @return
	 */
	public List<EndOption> getTransactionEndedOptionList(String endOption) {
		
		JSONObject transactionEnded = config.getJSONObject("TransactionEnded");
		JSONArray endOptionArray = null;
		try {
			endOptionArray = transactionEnded.getJSONArray(endOption);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return converToList(EndOption.class, endOptionArray);
		
	}
	
	public List<AddOfferOptions> getMyBookConditionList() {
		JSONArray mBookCondition = config.getJSONArray("MyBookCondition");
		return converToList(AddOfferOptions.class, mBookCondition);
	}
	
	public List<AddOfferOptions> getMyBookBinding() {
		JSONArray mBookBookbinding = config.getJSONArray("MyBookBookbinding");
		return converToList(AddOfferOptions.class, mBookBookbinding);
	}
	
	
	public String getBindingLocalized(String offerBindingCode) {
		List<AddOfferOptions> list = getMyBookBinding();
		return codeToName(list, offerBindingCode);
	}

	public String getConditionLocalized(String offerConditionCode) {
		List<AddOfferOptions> list = getMyBookConditionList();
		return codeToName(list, offerConditionCode);
	}
	

	private <T> List<T> converToList(Class<T> type, JSONArray array) {
		if(array != null){
			List<T> list = new ArrayList<T>();
			for (int i = 0; i < array.length(); i++) {
				
				JSONObject object = null;
				try {
					object = array.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(object != null){					
					T option = null;
					if(type == AddOfferOptions.class){
						option = (T) new AddOfferOptions(object);
					}
					if(type == EndOption.class){
						option = (T) new EndOption(object);
					}

					if(option != null){						
						list.add(option);
					}
					
				}
//				try {
//					T option = type.getConstructor(JSONObject.class).newInstance(array.getJSONObject(i));
//					list.add(option);
//				} catch (InstantiationException | IllegalAccessException
//						| IllegalArgumentException | InvocationTargetException
//						| NoSuchMethodException | JSONException e) {
//	
//					e.printStackTrace();
//				} 
			}
			return list;
		}else{
			return null;
		}
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

	
	public class AddOfferOptions {
		private String name;
		private String description;
		private String code;

		public AddOfferOptions(JSONObject jsonObject) {
			try {
				
				name = jsonObject.getString(
						"name");
				code = jsonObject.getString(
						"code");
				description = jsonObject.getString(
						"description");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getCode() {
			return code;
		}

		@Override
		public String toString() {
			return getName();
		}
		
		
	}

	
	

}
