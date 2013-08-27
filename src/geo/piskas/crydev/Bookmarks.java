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
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Bookmarks extends ListActivity {

	private ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.enableActionBar(this);

		try {
			Database bdb = new Database(this);
			bdb.open();
			bookmarks = bdb.getData();
			bdb.close();
			if (bookmarks.size() == 0) {
				Toast.makeText(this,
						getResources().getString(R.string.bookmarks_empty),
						Toast.LENGTH_SHORT).show();
				finish();
			} else {
				setListAdapter(new ArrayAdapter<String>(Bookmarks.this,
						android.R.layout.simple_list_item_1, getTitles()));
			}
			lv = getListView();
			lv.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> av, View v,
						int pos, long id) {
					showDeleteBookmark(pos);
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this,
					getResources().getString(R.string.something_worng),
					Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	private String[] getTitles() {
		String[] titles = new String[bookmarks.size()];
		int i = 0;
		for (Bookmark b : bookmarks) {
			titles[i] = b.getTitle();
			i += 1;
		}
		return titles;
	}

	private void showDeleteBookmark(final int pos) {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog
				.setTitle(getResources().getString(R.string.bookmark_delete));
		alertDialog.setMessage(getResources().getString(
				R.string.bookmark_delete_desc));
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.setCanceledOnTouchOutside(false);

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources()
				.getString(R.string.yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						if (Utils.deleteBookmarkFromDb(
								Long.parseLong(bookmarks.get(pos).getId()),
								Bookmarks.this)) {
							Intent i = new Intent(getIntent());
							finish();
							startActivity(i);
						}
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources()
				.getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Utils.showLoadingPd(this);
		String url = bookmarks.get(position).getUrl();
		if (url.startsWith("http://www.crydev.net/newspage.php")) {
			startActivity(new Intent(this, NewsPost.class)
					.putExtra("link", url));
		} else if (url.startsWith("http://www.crydev.net/viewforum.php")) {
			startActivity(new Intent(this, SubForum.class)
					.putExtra("link", url));
		} else if (url.startsWith("http://www.crydev.net/viewtopic.php")) {
			startActivity(new Intent(this, ForumThread.class).putExtra("link",
					url));
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			if (item.getItemId() == android.R.id.home) {
				finish();
			}
		}
		return true;
	}

	@Override
	protected void onPause() {
		Utils.dismissLoadingPd();
		super.onPause();
	}
}
