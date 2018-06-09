package mahmoud.com.atglance.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.InputStream;

public class Article implements Parcelable {
	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
		@Override
		public Article createFromParcel(Parcel in) {
			return new Article(in);
		}
		
		@Override
		public Article[] newArray(int size) {
			return new Article[size];
		}
	};
	public String id;
	@SerializedName("source")
	@Expose
	private Source source;
	@SerializedName("author")
	@Expose
	private String author;
	@SerializedName("title")
	@Expose
	private String title;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("url")
	@Expose
	private String url;
	@SerializedName("urlToImage")
	@Expose
	private String urlToImage;
	@SerializedName("publishedAt")
	@Expose
	private String publishedAt;
	
	public Article(InputStream in) {
	}
	
	public Article(String id, String title, String description, String source, String image, String link, String publishedAt) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.source = new Source("", source);
		//  this.source.setName(source);;
		this.urlToImage = image;
		this.url = link;
		this.publishedAt = publishedAt;
	}
	
	protected Article(Parcel in) {
		source = (Source) in.readValue(Source.class.getClassLoader());
		author = in.readString();
		title = in.readString();
		description = in.readString();
		url = in.readString();
		urlToImage = in.readString();
		publishedAt = in.readString();
	}
	
	public Source getSource() {
		return source;
	}
	
	public void setSource(Source source) {
		this.source = source;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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
	
	public String getUrlToImage() {
		return urlToImage;
	}
	
	public void setUrlToImage(String urlToImage) {
		this.urlToImage = urlToImage;
	}
	
	public String getPublishedAt() {
		return publishedAt;
	}
	
	public void setPublishedAt(String publishedAt) {
		this.publishedAt = publishedAt;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(source);
		dest.writeString(author);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(url);
		dest.writeString(urlToImage);
		dest.writeString(publishedAt);
	}
}
