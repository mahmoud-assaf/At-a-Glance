package mahmoud.com.atglance.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Source implements Parcelable {
	
	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
		@Override
		public Source createFromParcel(Parcel in) {
			return new Source(in);
		}
		
		@Override
		public Source[] newArray(int size) {
			return new Source[size];
		}
	};
	@SerializedName("id")
	@Expose
	private Object id;
	@SerializedName("name")
	@Expose
	private String name;
	
	public Source(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	protected Source(Parcel in) {
		id = (Object) in.readValue(Object.class.getClassLoader());
		name = in.readString();
	}
	
	public Object getId() {
		return id;
	}
	
	public void setId(Object id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(id);
		dest.writeString(name);
	}
}
