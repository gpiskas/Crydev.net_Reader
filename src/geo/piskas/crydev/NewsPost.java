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
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class NewsPost extends Activity {

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
				Utils.makeVideoLinks(doc, getApplicationContext());
				Utils.loadHtml(webview, editPage());
			} catch (Exception e) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(getIntent().getStringExtra("link"))));
				Log.e("Page Editing Error - NewsPost", e.toString());
				e.printStackTrace();
				finish();
			}
		} catch (Exception e) {
			Utils.showConnectionErrorPopup(webview, this);
			Log.e("Connection Error - NewsPost", e.toString());
			e.printStackTrace();
		}
	}

	private String editPage() {
		doc.select("link").remove();
		doc.select("title").after(
				"<link rel=\"stylesheet\" href=\"" + Utils.CSS_URL
						+ "\" type=\"text/css\" />");
		setTitle(getResources().getString(R.string.title_news) + " - " + doc.select("title").text());
		doc.select("script").remove();
		doc.select("style").remove();

		String newscontent = doc.select("div.content_news").toString();
		String divider = doc.select("hr.divider").toString();
		Element readmore = doc.select("a.readmore").first();
		readmore.attr("style",
				"font-size: 16px; display: block; text-align: center;");
		readmore.prepend("<br>");
		doc.select("img[alt=See all news]")
				.attr("src",
						"http://www.crydev.net/styles/twilightBB/imageset/arrowOrange14px.png");
		doc.body().empty();
		doc.body().append(newscontent);
		doc.body().append(divider);
		doc.body().append(readmore.toString());

		doc.select("[type=application/x-shockwave-flash]").remove();
		doc.select("link")
				.after("<script type=\"text/javascript\">function reimg(image){if (image.clientWidth >= screen.width){image.style['width'] = '100%';}}</script>");

		String html = doc.html();
		html = html.replaceAll("\\./", "http://www.crydev.net/");
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
