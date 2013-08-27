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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.StrictMode;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Utils {

	public static final String CSS_URL = "style.css";
	public static final String SHARED_PREFS = "SHAREDPREFS";
	private static AlertDialog pd;

	@TargetApi(14)
	public static void enableActionBar(Activity act) {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			act.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@TargetApi(9)
	public static void setStrictPolicy() {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
			// do nothing
		} else {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
		}
	}

	@TargetApi(13)
	public static void loadHtml(WebView webview, String html) {
		webview.loadDataWithBaseURL("file:///android_asset/", html,
				"text/html", "utf-8", null);
	}

	public static void showLoadingPd(final Context c) {
		pd = new AlertDialog.Builder(c).create();
		pd.setTitle(c.getResources().getString(R.string.loading));
		pd.setIcon(R.drawable.ic_launcher);
		pd.show();

	}

	public static void dismissLoadingPd() {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	public static void setUpWebView(WebView webview, final Activity context) {
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webview.setScrollbarFadingEnabled(true);
		webview.setWebChromeClient(new WebChromeClient());
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showLoadingPd(context);
				if (url.equals("http://www.crydev.net/index.php")
						|| url.equals("http://www.crydev.net")
						|| url.equals("http://www.crydev.net/")) {
					context.startActivity(new Intent(context, Crydev.class));
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
					context.startActivity(new Intent(context, ForumThread.class)
							.putExtra("link", url));
				} else if (url
						.startsWith("http://www.crydev.net/posting.php?mode=reply")) {
					context.startActivityForResult(new Intent(context,
							PostReply.class).putExtra("link", url), 1);
				} else if (url
						.startsWith("http://www.crydev.net/memberlist.php")) {
					dismissLoadingPd();
				} else {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(url)));
				}
				return true;
			}
		});
	}

	public static boolean connectedToInternet(Activity act) {
		if (((ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo() != null) {
			return true;
		} else {
			return false;
		}

	}

	public static void showConnectionErrorPopup(final WebView webview,
			final Activity act) {
		webview.setVisibility(LinearLayout.INVISIBLE);
		final AlertDialog alertDialog = new AlertDialog.Builder(act).create();
		alertDialog.setTitle(act.getResources().getString(
				R.string.connection_error));
		alertDialog.setMessage(act.getResources().getString(
				R.string.try_again_later));
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.setCanceledOnTouchOutside(false);

		alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				act.finish();
			}
		});

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, act.getResources()
				.getString(R.string.exit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						act.finish();
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, act.getResources()
				.getString(R.string.retry),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (connectedToInternet(act)) {
							Intent i = new Intent(act.getIntent());
							act.finish();
							act.startActivity(i);
							Toast.makeText(
									act,
									act.getResources().getString(
											R.string.connected),
									Toast.LENGTH_SHORT).show();
						} else {
							showConnectionErrorPopup(webview, act);
						}
					}
				});
		alertDialog.show();
	}

	public static void showIncorrectLoginAndRestart(final Activity act,
			final String post) {
		final AlertDialog alertDialog = new AlertDialog.Builder(act).create();
		alertDialog.setTitle(act.getResources().getString(
				R.string.invalid_login));
		alertDialog.setMessage(act.getResources().getString(
				R.string.invalid_login_desc));
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.setCanceledOnTouchOutside(false);

		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, act.getResources()
				.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
				act.finish();
				act.startActivity(act.getIntent().putExtra("backup", post));
			}
		});
		alertDialog.show();
	}

	public static void makeVideoLinks(Document doc, Context c) {
		Elements vids = doc.select("[type=application/x-shockwave-flash]");
		for (Element vid : vids) {
			if (!(vid.attr("src").length() == 0)) {
				vid.after("<div><a style=\"color: #F15832;\" href=\""
						+ vid.attr("src") + "\" class=\"postlink\"><center><b>"
						+ c.getResources().getString(R.string.video_link)
						+ "</b></center></a></div>");
			} else {
				vid.after("<div><a style=\"color: #F15832;\" href=\""
						+ vid.select("param").attr("value")
						+ "\" class=\"postlink\"><center><b>"
						+ c.getResources().getString(R.string.video_link)
						+ "</b></center></a></div>");
			}
			vid.remove();
		}
	}

	public static Document getData(Intent i) throws Exception {
		if (i.hasExtra("cache")) {
			return Jsoup.parse((i.getStringExtra("cache")));
		} else {
			return Jsoup.connect(i.getStringExtra("link")).get();
		}
	}

	@SuppressWarnings("deprecation")
	@TargetApi(13)
	public static int getWidthDp(Activity act) {
		float density = act.getApplicationContext().getResources()
				.getDisplayMetrics().density;
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			act.getWindowManager().getDefaultDisplay().getSize(size);
			return (int) ((int) size.x / density);
		} else {
			return (int) ((int) act.getWindowManager().getDefaultDisplay()
					.getWidth() / density);
		}
	}

	public static int convertDpToPx(int dp, Context context) {
		return (int) (dp * context.getResources().getDisplayMetrics().density);
	}

	public static int convertPxToDp(int px, Context context) {
		return (int) (px / context.getResources().getDisplayMetrics().density);
	}

	public static void keepScreenOn(Activity act) {
		if (act.getSharedPreferences(Utils.SHARED_PREFS, 0).getBoolean(
				"keepscreenon", false) == true) {
			act.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			act.getWindow()
					.clearFlags(
							android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	public static void addBookmarkToDb(String title, String url, Context c) {

		try {
			Database entry = new Database(c);
			entry.open();
			entry.createEntry(title, url.replaceAll("&sid=[0-9a-zA-Z]*", ""), c);
			entry.close();
			Toast.makeText(c, c.getResources().getString(R.string.bookmarked),
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(c,
					c.getResources().getString(R.string.something_worng),
					Toast.LENGTH_SHORT).show();
		}
	}

	public static boolean deleteBookmarkFromDb(long row, Context c) {

		try {
			Database entry = new Database(c);
			entry.open();
			entry.deleteEntry(row);
			entry.close();
			Toast.makeText(c,
					c.getResources().getString(R.string.bookmark_deleted),
					Toast.LENGTH_SHORT).show();
			return true;
		} catch (Exception e) {
			Toast.makeText(c,
					c.getResources().getString(R.string.something_worng),
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

}
