package mahmoud.com.atglance.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SourcesResponse implements Parcelable {
	
	public final static Creator<SourcesResponse> CREATOR = new Creator<SourcesResponse>() {
		
		@SuppressWarnings({
			"unchecked"
		})
		public SourcesResponse createFromParcel(Parcel in) {
			return new SourcesResponse(in);
		}
		
		public SourcesResponse[] newArray(int size) {
			return (new SourcesResponse[size]);
		}
		
	};
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("sources")
	@Expose
	private List<SubSource> sources = null;
	
	protected SourcesResponse(Parcel in) {
		this.status = ((String) in.readValue((String.class.getClassLoader())));
		in.readList(this.sources, (SubSource.class.getClassLoader()));
	}
	
	public SourcesResponse() {
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<SubSource> getSources() {
		return sources;
	}
	
	public void setSources(List<SubSource> sources) {
		this.sources = sources;
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(status);
		dest.writeList(sources);
	}
	
}