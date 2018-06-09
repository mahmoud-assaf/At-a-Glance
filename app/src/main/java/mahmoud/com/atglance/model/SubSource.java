package mahmoud.com.atglance.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubSource implements Parcelable {
	
	public final static Creator<SubSource> CREATOR = new Creator<SubSource>() {
		
		@SuppressWarnings({
			"unchecked"
		})
		public SubSource createFromParcel(Parcel in) {
			return new SubSource(in);
		}
		
		public SubSource[] newArray(int size) {
			return (new SubSource[size]);
		}
		
	};
	boolean isSelected = false;
	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("url")
	@Expose
	private String url;
	@SerializedName("category")
	@Expose
	private String category;
	@SerializedName("language")
	@Expose
	private String language;
	@SerializedName("country")
	@Expose
	private String country;
	
	protected SubSource(Parcel in) {
		this.id = ((String) in.readValue((String.class.getClassLoader())));
		this.name = ((String) in.readValue((String.class.getClassLoader())));
		this.description = ((String) in.readValue((String.class.getClassLoader())));
		this.url = ((String) in.readValue((String.class.getClassLoader())));
		this.category = ((String) in.readValue((String.class.getClassLoader())));
		this.language = ((String) in.readValue((String.class.getClassLoader())));
		this.country = ((String) in.readValue((String.class.getClassLoader())));
	}
	
	public SubSource() {
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(id);
		dest.writeValue(name);
		dest.writeValue(description);
		dest.writeValue(url);
		dest.writeValue(category);
		dest.writeValue(language);
		dest.writeValue(country);
	}
	
}