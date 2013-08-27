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
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ForumThread extends Activity {

	private WebView webview;
	private Document doc;
	private int WIDTH_DP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WIDTH_DP = Utils.getWidthDp(this);

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
				adjustWebview(this);
				Utils.makeVideoLinks(doc, getApplicationContext());
				Utils.loadHtml(webview, editPage());
			} catch (Exception e) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(getIntent().getStringExtra("link"))));
				Log.e("Page Editing Error - ForumThread", e.toString());
				e.printStackTrace();
				finish();
			}
		} catch (Exception e) {
			Utils.showConnectionErrorPopup(webview, this);
			Log.e("Connection Error - ForumThread", e.toString());
			e.printStackTrace();
		}
	}

	private void adjustWebview(final Activity context) {
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Utils.showLoadingPd(context);
				if (url.equals("http://www.crydev.net/index.php")
						|| url.equals("http://www.crydev.net")
						|| url.equals("http://www.crydev.net/")) {
					startActivity(new Intent(ForumThread.this, Crydev.class));
				} else if (url.equals("http://www.crydev.net/newspage.php")) {
					context.startActivity(new Intent(context, NewsHome.class));
				} else if (url
						.startsWith("http://www.crydev.net/newspage.php?news=")) {
					context.startActivity(new Intent(context, NewsPost.class)
							.putExtra("link", url));
				} else if (url.startsWith("http://www.crydev.net/forum.php")
						|| url.startsWith("http://www.crydev.net/viewforum.php?f=329")) {
					context.startActivity(new Intent(context, ForumHome.class));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=331")) {
					context.startActivity(new Intent(context, ForumHome.class)
							.putExtra("scroll", 1));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=295")) {
					context.startActivity(new Intent(context, ForumHome.class)
							.putExtra("scroll", 2));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=307")) {
					context.startActivity(new Intent(context, Ce3_1.class));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=313")) {
					context.startActivity(new Intent(context, Ce3_2.class));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=320")) {
					context.startActivity(new Intent(context, Ce3_3.class));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=306")) {
					context.startActivity(new Intent(context, Ce2_1.class));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=298")) {
					context.startActivity(new Intent(context, Ce2_2.class));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=299")) {
					context.startActivity(new Intent(context, Ce2_3.class));
				} else if (url
						.startsWith("http://www.crydev.net/viewforum.php?f=")) {
					context.startActivity(new Intent(context, SubForum.class)
							.putExtra("link", url));
				} else if (url
						.startsWith("http://www.crydev.net/viewtopic.php")) {
					try {
						doc = new Document("");
						if (Utils.connectedToInternet(ForumThread.this)) {
							getIntent().putExtra("link", url);
							doc = Utils.getData(getIntent());
						} else {
							throw new Exception("Not Connected");
						}
						try {
							Utils.makeVideoLinks(doc, getApplicationContext());
							Utils.loadHtml(webview, editPage());
							Utils.dismissLoadingPd();
						} catch (Exception e) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(url)));
							Log.e("Page Editing Error - ForumThread",
									e.toString());
							e.printStackTrace();
							finish();
						}
					} catch (Exception e) {
						Utils.showConnectionErrorPopup(webview,
								ForumThread.this);
						Log.e("Connection Error - ForumThread", e.toString());
						e.printStackTrace();
					}
				} else if (url
						.startsWith("http://www.crydev.net/posting.php?mode=reply")) {
					context.startActivityForResult(new Intent(context,
							PostReply.class).putExtra("link", url), 1);
				} else if (url
						.startsWith("http://www.crydev.net/memberlist.php")) {
					Utils.dismissLoadingPd();
				} else {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(url)));
				}
				return true;
			}
		});
	}

	private String editPage() {
		doc.select("link").remove();
		doc.select("title").after(
				"<link rel=\"stylesheet\" href=\"" + Utils.CSS_URL
						+ "\" type=\"text/css\" />");
		setTitle(getResources().getString(R.string.title_thread) + " - " + doc.select("title").text());
		doc.select("script").remove();
		doc.select("style").remove();

		String breadcrumbs = doc.select("p.breadcrumbs").toString();
		String pagecontent = doc.select("div#pagecontent").toString();
		String pagination = "<div class=\"pagination\">"
				+ doc.select("td.nav").select("span.pagination")
						.select("a:has(span.pagination_left)") + "</div>";
		doc.body().empty();
		doc.body().prepend(breadcrumbs);
		doc.select("p.breadcrumbs")
				.after("<hr style=\"background-color:#F15832;position:relative;top:-1px;\">");
		doc.body().append(pagecontent);
		doc.body().append(pagination);
		doc.select("div.pagination")
				.prepend(
						"<hr style=\"background-color:#F15832;position:relative;top:-1px;\">");

		String replyButton = "";
		if (doc.select("a.locked-icon").isEmpty()) {
			replyButton = doc.select("a.reply-icon").first().toString();
		}
		doc.select("table").first().remove();
		String link = getIntent().getStringExtra("link").replaceFirst(
				"&start=\\d+", "");
		doc.select("div.pagination")
				.after("<div align=\"center\"><a class=\"pagination\" href="
						+ link
						+ "&start=0>First</a> ... <a class=\"pagination\" href="
						+ link + "&start=1000000>Last</a>" + replyButton
						+ "</div>");
		doc.select("table.tablebg").select("tr").first().remove();

		Elements namespace = doc
				.select("div[style=float: left;padding-left:20px;]");
		int i = 0;
		final int wh = WIDTH_DP / 14;
		final int pos = WIDTH_DP / 55;
		for (Element pr : doc.select("td.profile")) {
			namespace.get(i).text("");
			if (pr.select("img[alt=User avatar]").size() != 0) {
				Element img = pr.select("img[alt=User avatar]").first();
				img.attr("width", wh + "px");
				img.attr("height", wh + "px");
				img.attr("align", "middle");
				img.attr("style", "position:relative; BOTTOM:" + pos + "px;");
				namespace.get(i).append(
						pr.select("td.postdetails:has(a.postauthor)").first()
								.prepend(img.toString()).toString());

			} else {
				namespace.get(i).append(
						pr.select("td.postdetails:has(a.postauthor)").first()
								.toString());
			}
			i++;
		}

		// SIGS
		if (getSharedPreferences(Utils.SHARED_PREFS, 0).getBoolean(
				"togglesigs", true) == false) {
			doc.select("span:has(div.signature)").remove();
		}
		doc.select("span:has(div.signature)")
				.select("hr")
				.attr("style",
						"border-bottom:1px solid #58595B;margin-bottom: 10px;margin-top: 10px;margin-left:20px;margin-right:20px;");

		doc.select("td.profile").remove();

		doc.select("link").after(
				"<style>td {max-width: " + WIDTH_DP
						+ "px;} .signature {max-height: " + WIDTH_DP / 3
						+ "px; overflow: auto;}</style>");
		doc.select("link")
				.after("<script type=\"text/javascript\">function reimg(image){if (image.clientWidth >= screen.width){image.style['width'] = '100%';}}</script>");
		doc.select("div.content_forum").select("img[alt=Image]")
				.attr("onclick", "window.open(this.src);return false;");

		doc.select("span:contains(Downloaded)").remove();
		doc.select("a:has(img[src$=icon_post_target.gif])").remove();

		doc.select("td[valign=middle]").remove();
		doc.select("td[colspan=2]").remove();

		doc.select(
				"td[style=border-bottom:1px solid #58595B;border-right:1px solid #58595b;]")
				.remove();
		doc.select("td[valign=middle]").remove();
		doc.select("td[height=5px]").remove();

		doc.select("td.edit_area")
				.before("<tr><td colspan=\"2\"><hr style=\"background-color:#008DC1;position:relative;top:-1px;\"></td></tr>");
		doc.select("td.edit_area").remove();

		doc.select("hr").last().remove();

		String html = doc.html();
		html = html.replaceAll("\\./", "http://www.crydev.net/");
		html = html.replaceAll("Posted", "");
		html = html.replaceAll("Attachments:", "");
		html = html.replaceAll("<div style=\"float:left\">",
				"<div style=\"width=100%\">");
		html = html.replaceAll("border-right:1px solid #58595b;", "");
		html = html.replaceAll("padding-left:20px;", "padding-left:4px;");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		Utils.dismissLoadingPd();
		super.onPause();
	}

}
