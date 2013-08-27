/*
*  Crydev.net Reader
*  Copyright (C) 2013  George Piskas
*
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
*  Contact: geopiskas@gmail.com
*/

package geo.piskas.crydev;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";

	private static final String DATABASE_NAME = "DB_Bookmarks";
	private static final String DATABASE_TABLE = "TBL_Bookmarks";
	private static final int DATABASE_VERSION = 1;

	private DbHelper dbHelper;
	private final Context context;
	private SQLiteDatabase db;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE
					+ " TEXT NOT NULL UNIQUE, " + KEY_URL
					+ " TEXT NOT NULL UNIQUE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	public Database(Context c) {
		context = c;
	}

	public Database open() throws SQLException {
		dbHelper = new DbHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long createEntry(String title, String url, Context context) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TITLE, title);
		cv.put(KEY_URL, url);
		return db.insert(DATABASE_TABLE, null, cv);
	}

	public void deleteEntry(long lRow1) throws SQLException {
		db.delete(DATABASE_TABLE, KEY_ROWID + "=" + lRow1, null);
	}

	public ArrayList<Bookmark> getData() {
		String[] columns = new String[] { KEY_ROWID, KEY_TITLE, KEY_URL };
		Cursor c = db.query(DATABASE_TABLE, columns, null, null, null, null,
				null);

		int iRow = c.getColumnIndex(KEY_ROWID);
		int iTitle = c.getColumnIndex(KEY_TITLE);
		int iUrl = c.getColumnIndex(KEY_URL);

		ArrayList<Bookmark> result = new ArrayList<Bookmark>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result.add(new Bookmark(c.getString(iRow), c.getString(iTitle), c
					.getString(iUrl)));
		}

		return result;
	}
}
