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

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class SubForum extends Activity {

	private WebView webview;
	private Document doc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.setStrictPolicy();

		setContentView(R.layout.webview);
		Utils.keepScreenOn(this);
		Utils.enableActionBar(this);
		webview = (WebView) findViewById(R.id.webview);

		try {
			doc = new Document("");
			if (Utils.connectedToInternet(this)) {
				doc = Utils.getData(getIntent());
			} else {
				throw new Exception("Not Connected");
			}
			try {
				Utils.setUpWebView(webview, this);
				Utils.loadHtml(webview, editPage());

			} catch (Exception e) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(getIntent().getStringExtra("link"))));
				Log.e("Page Editing Error - SubForum", e.toString());
				e.printStackTrace();
				finish();
			}
		} catch (Exception e) {
			Utils.showConnectionErrorPopup(webview, this);
			Log.e("Connection Error - SubForum", e.toString());
			e.printStackTrace();
		}
	}

	private String editPage() {
		doc.select("link").remove();
		doc.select("title").after(
				"<link rel=\"stylesheet\" href=\"" + Utils.CSS_URL
						+ "\" type=\"text/css\" />");
		setTitle(getResources().getString(R.string.title_forum) + " - " + doc.select("title").text());
		doc.select("script").remove();
		doc.select("style").remove();

		String breadcrumbs = doc.select("p.breadcrumbs").toString();
		String pagecontent = doc.select("div#pagecontent").toString();
		doc.body().empty();
		doc.body().prepend(breadcrumbs);
		doc.body().append(pagecontent);

		doc.select("table").first().remove();
		doc.select("table").select("tr").first().remove();
		doc.body().append(
				"<div class=\"pagination\">"
						+ doc.select("a:has(span.pagination_left)").toString()
						+ "</div>");
		doc.select("table").last().remove();
		doc.select("table").last().remove();

		doc.select("td[width=15]").attr("width", "2");
		doc.select("td.row2").remove();
		doc.select("td.row_h").attr("width", "35");

		Elements row1 = doc.select("table.tablebg").select("td.row1");
		try {
			int i = 2;
			while (true) {
				row1.get(i - 1).select("p.topicdetails").empty();
				row1.get(i - 1).select("img[title=Attachment(s)]").remove();
				try {
					row1.get(i - 1).select("a").get(1).before("<br>(");
				} catch (Exception e) {
					e.printStackTrace();
				}

				String lastpost = row1.get(i + 1).text()
						.replaceAll("\\.\\d\\d\\d\\d", "");
				int index = lastpost.indexOf("by");
				row1.get(i - 1)
						.select("p.topicdetails")
						.after("Last post " + lastpost.substring(index) + ", "
								+ lastpost.substring(0, index));

				row1.get(i).remove();
				row1.get(i + 1).remove();
				row1.get(i + 2).remove();
				i += 5;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = doc.html();
		html = html.replaceAll("&nbsp; \\(", "");
		html = html.replaceAll("\\./", "http://www.crydev.net/");
		html = html
				.replaceAll("&nbsp;-&nbsp;\\d+\\.\\d+\\.\\d+, \\d+:\\d+", "");
		return html;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			getMenuInflater().inflate(R.menu.menuwebview, menu);
		} else {
			getMenuInflater().inflate(R.menu.menuwebview_old, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString()
				.equals(getResources().getString(R.string.home))) {
			startActivity(new Intent(this, Crydev.class));
		} else if (item.getTitle().toString()
				.equals(getResources().getString(R.string.refresh))) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		} else if (item.getTitle().toString()
				.equals(getResources().getString(R.string.bookmark))) {
			Utils.addBookmarkToDb((String) getTitle(), getIntent()
					.getStringExtra("link"), this);
		}
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
