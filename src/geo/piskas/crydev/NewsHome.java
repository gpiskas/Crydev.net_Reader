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

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class NewsHome extends Activity implements OnClickListener {

	private static final String SOURCE_NEWS = "http://www.crydev.net/newspage.php?start=0";

	private TextView listTitle[], listContent[], tvReadMore;
	private ImageView listIcon[];
	private LinearLayout lay1, lay2, lay3, lay4, lay5;
	private Elements link, title, content_small, icons;
	private ScrollView scroll;
	private static String links[];
	private static int page5 = 0;
	private static Document doc, c0, c1, c2, c3, c4, cfw, cbk;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.setStrictPolicy();
		setContentView(R.layout.newshome);
		Utils.enableActionBar(this);

		init();
		fetchNews(SOURCE_NEWS, false, false);

	}

	private void fetchNews(final String source, boolean back, boolean noCache) {
		try {
			if (!Utils.connectedToInternet(this)) {
				throw new Exception("Not Connected");
			}

			if (!noCache && !back && cfw.hasText()) {
				doc = cfw.clone();
			} else if (!noCache && back && cbk.hasText()) {
				doc = cbk.clone();
			} else {
				doc = Jsoup.connect(source).get();
			}

			c0.empty();
			c1.empty();
			c2.empty();
			c3.empty();
			c4.empty();

			link = doc.select("div.news_big").select("a[href]");
			links[0] = link.get(0).attr("href").toString()
					.replaceFirst("\\./", "http://www.crydev.net/");
			links[1] = link.get(1).attr("href").toString()
					.replaceFirst("\\./", "http://www.crydev.net/");
			links[2] = link.get(2).attr("href").toString()
					.replaceFirst("\\./", "http://www.crydev.net/");
			links[3] = link.get(3).attr("href").toString()
					.replaceFirst("\\./", "http://www.crydev.net/");
			links[4] = link.get(4).attr("href").toString()
					.replaceFirst("\\./", "http://www.crydev.net/");

			cacheFwBk(source);
			cacheNews();

			title = doc.select("div.news_top");
			content_small = doc.select("div.content_small");
			icons = doc.select("div.news_big");

			for (int i = 0; i < 5; i++) {
				listTitle[i].setText(title.get(i).text()
						.substring(0, title.get(i).text().indexOf("Posted")));

				Pattern p = Pattern
						.compile("/images/news/medium/\\d+\\.(png|jpg|gif)");
				Matcher m = p.matcher(icons.get(i).toString());
				m.find();
				String iconUrl = "http://www.crydev.net" + m.group();
				try {
					Bitmap bitmap = BitmapFactory
							.decodeStream((InputStream) new URL(iconUrl)
									.getContent());
					listIcon[i].setImageBitmap(bitmap);
				} catch (Exception e) {
					Log.e("Fetch News Error - NewsHome.fetchNews", e.toString());
					e.printStackTrace();
				}
				listContent[i].setText(content_small.get(i).text());
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
					listContent[i].setEllipsize(TextUtils.TruncateAt.END);
				}
			}

			scroll.smoothScrollTo(0, 0);
			lay1.setVisibility(LinearLayout.VISIBLE);
			lay2.setVisibility(LinearLayout.VISIBLE);
			lay3.setVisibility(LinearLayout.VISIBLE);
			lay4.setVisibility(LinearLayout.VISIBLE);
			lay5.setVisibility(LinearLayout.VISIBLE);
			tvReadMore.setVisibility(LinearLayout.VISIBLE);

		} catch (Exception e) {
			showConnectionErrorPopup();
			Log.e("Connection Error - NewsHome.fetchNews", e.toString());
			e.printStackTrace();
		}
	}

	private void cacheFwBk(final String source) {
		new Thread(new Runnable() {
			public void run() {
				try {
					cfw = Jsoup.connect(
							source.replaceFirst("start=\\d+", "start="
									+ (page5 + 5))).get();
					if (page5 >= 5) {
						cbk = Jsoup.connect(
								source.replaceFirst("start=\\d+", "start="
										+ (page5 - 5))).get();
					}
				} catch (Exception e) {
					Log.w("Caching Error - NewsHome.cacheFwBk", e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void cacheNews() {
		new Thread(new Runnable() {
			public void run() {
				try {
					c0 = Jsoup.connect(links[0]).get();
					c1 = Jsoup.connect(links[1]).get();
					c2 = Jsoup.connect(links[2]).get();
					c3 = Jsoup.connect(links[3]).get();
					c4 = Jsoup.connect(links[4]).get();
				} catch (Exception e) {
					Log.w("Caching Error - NewsHome.cacheNews", e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void showConnectionErrorPopup() {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getResources()
				.getString(R.string.connection_error));
		alertDialog.setMessage(getResources().getString(
				R.string.try_again_later));
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.setCanceledOnTouchOutside(false);

		alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources()
				.getString(R.string.exit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources()
				.getString(R.string.retry),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						fetchNews(
								SOURCE_NEWS.replaceFirst("start=\\d+", "start="
										+ page5), false, true);
					}
				});
		alertDialog.show();
	}

	private void init() {
		links = new String[5];
		c0 = new Document("");
		c1 = new Document("");
		c2 = new Document("");
		c3 = new Document("");
		c4 = new Document("");
		cfw = new Document("");
		cbk = new Document("");
		listContent = new TextView[5];
		listContent[0] = (TextView) findViewById(R.id.content_small1);
		listContent[1] = (TextView) findViewById(R.id.content_small2);
		listContent[2] = (TextView) findViewById(R.id.content_small3);
		listContent[3] = (TextView) findViewById(R.id.content_small4);
		listContent[4] = (TextView) findViewById(R.id.content_small5);
		listTitle = new TextView[5];
		listTitle[0] = (TextView) findViewById(R.id.title1);
		listTitle[1] = (TextView) findViewById(R.id.title2);
		listTitle[2] = (TextView) findViewById(R.id.title3);
		listTitle[3] = (TextView) findViewById(R.id.title4);
		listTitle[4] = (TextView) findViewById(R.id.title5);
		listIcon = new ImageView[5];
		listIcon[0] = (ImageView) findViewById(R.id.icon1);
		listIcon[1] = (ImageView) findViewById(R.id.icon2);
		listIcon[2] = (ImageView) findViewById(R.id.icon3);
		listIcon[3] = (ImageView) findViewById(R.id.icon4);
		listIcon[4] = (ImageView) findViewById(R.id.icon5);
		scroll = (ScrollView) findViewById(R.id.scrollnews);
		lay1 = (LinearLayout) findViewById(R.id.lay1);
		lay2 = (LinearLayout) findViewById(R.id.lay2);
		lay3 = (LinearLayout) findViewById(R.id.lay3);
		lay4 = (LinearLayout) findViewById(R.id.lay4);
		lay5 = (LinearLayout) findViewById(R.id.lay5);
		tvReadMore = (TextView) findViewById(R.id.tvreadmore);
		lay1.setOnClickListener(this);
		lay2.setOnClickListener(this);
		lay3.setOnClickListener(this);
		lay4.setOnClickListener(this);
		lay5.setOnClickListener(this);
		tvReadMore.setOnClickListener(this);
		lay1.setVisibility(LinearLayout.INVISIBLE);
		lay2.setVisibility(LinearLayout.INVISIBLE);
		lay3.setVisibility(LinearLayout.INVISIBLE);
		lay4.setVisibility(LinearLayout.INVISIBLE);
		lay5.setVisibility(LinearLayout.INVISIBLE);
		tvReadMore.setVisibility(LinearLayout.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		Intent newsPost = new Intent(this, NewsPost.class);
		if (v.getId() == R.id.tvreadmore) {
			page5 += 5;
			fetchNews(SOURCE_NEWS.replaceFirst("start=\\d+", "start=" + page5),
					false, false);
		} else {
			Utils.showLoadingPd(this);
			switch (v.getId()) {
			case R.id.lay1:
				if (c0.hasText()) {
					newsPost.putExtra("cache", c0.html()).putExtra("link",
							links[0]);
				} else {
					newsPost.putExtra("link", links[0]);
				}
				startActivity(newsPost);
				break;
			case R.id.lay2:
				if (c1.hasText()) {
					newsPost.putExtra("cache", c1.html()).putExtra("link",
							links[1]);
				} else {
					newsPost.putExtra("link", links[1]);
				}
				startActivity(newsPost);
				break;
			case R.id.lay3:
				if (c2.hasText()) {
					newsPost.putExtra("cache", c2.html()).putExtra("link",
							links[2]);
				} else {
					newsPost.putExtra("link", links[2]);
				}
				startActivity(newsPost);
				break;
			case R.id.lay4:
				if (c3.hasText()) {
					newsPost.putExtra("cache", c3.html()).putExtra("link",
							links[3]);
				} else {
					newsPost.putExtra("link", links[3]);
				}
				startActivity(newsPost);
				break;
			case R.id.lay5:
				if (c4.hasText()) {
					newsPost.putExtra("cache", c4.html()).putExtra("link",
							links[4]);
				} else {
					newsPost.putExtra("link", links[4]);
				}
				startActivity(newsPost);
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (page5 >= 5) {
			page5 -= 5;
			fetchNews(SOURCE_NEWS.replaceFirst("start=\\d+", "start=" + page5),
					true, false);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.newsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString()
				.equals(getResources().getString(R.string.home))) {
			startActivity(new Intent(this, Crydev.class));
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
