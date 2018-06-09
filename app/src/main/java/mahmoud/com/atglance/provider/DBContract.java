package mahmoud.com.atglance.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class DBContract {
	public static final String AUTHORITY = "mahmoud.com.atglance.provider.Articles";
	public static final String PATH = "/articles";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + PATH);
	public static final String CONTENT_ARTICLE_LIST = "vnd.android.cursor.dir/vnd.mahmoud.com.atglance.articles";
	public static final String CONTENT_ARTICLE_ITEM = "vnd.android.cursor.item/vnd.mahmoud.com.atglance.articles";
	public static final String DATABASE_NAME = "articles";
	public static final int DATABASE_VERSION = 1;

	public static class ARTICLE implements BaseColumns {
		public static final String TABLE_NAME = "articles";
		public static final String ID = "_id";
		public static final String TITLE = "title";
		public static final String DESCRIPTION = "description";
		public static final String URL = "url";
		public static final String IMAGEURL = "imageurl";
		public static final String SOURCE = "source";
		public static final String PUBLISHEDAT = "publishedat";

		private ARTICLE() {
		}
	}
}
