package mahmoud.com.atglance.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDatabaseHelper extends SQLiteOpenHelper {
	public SqliteDatabaseHelper(Context context) {
		super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String CREATE_PRODUCTS_TABLE = "CREATE    TABLE " + DBContract.ARTICLE.TABLE_NAME + "("
			+ DBContract.ARTICLE.ID + " INTEGER PRIMARY KEY,"
			+ DBContract.ARTICLE.TITLE + " TEXT,"
			+ DBContract.ARTICLE.SOURCE + " TEXT,"
			+ DBContract.ARTICLE.URL + " TEXT,"
			+ DBContract.ARTICLE.IMAGEURL + " TEXT,"
			+ DBContract.ARTICLE.PUBLISHEDAT + " TEXT,"
			+ DBContract.ARTICLE.DESCRIPTION + " TEXT" + ")";
		sqLiteDatabase.execSQL(CREATE_PRODUCTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.ARTICLE.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}
}
