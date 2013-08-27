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

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ForumHome extends Activity implements OnClickListener {

	private static final String GENERAL_DISCUSSION = "http://www.crydev.net/viewforum.php?f=126";
	private static final String WEBSITE_SUPPORT = "http://www.crydev.net/viewforum.php?f=125";
	private static final String TUTORIALS = "http://www.crydev.net/viewforum.php?f=291";

	private static Document cSite, cGen, cTuts;

	private ScrollView scroll;
	private LinearLayout layce3_1, layce3_2, layce3_3, layce2_1, layce2_2,
			layce2_3, laycom_1, laycom_2, laycom_3;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.setStrictPolicy();
		setContentView(R.layout.forumhome);
		Utils.enableActionBar(this);
		cacheSubforums();

		init();

		if (getIntent().hasExtra("scroll")) {
			final int id = getIntent().getIntExtra("scroll", 0);
			if (id != 0) {
				OnGlobalLayoutListener listener = new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						scroll.smoothScrollTo(0, getScrollDistance(id));
					}
				};
				scroll.getViewTreeObserver()
						.addOnGlobalLayoutListener(listener);
			}
		}

	}

	private int getScrollDistance(int code) {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if (code == 1) {
			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				return 259;

			case DisplayMetrics.DENSITY_MEDIUM:
				return 345;

			case DisplayMetrics.DENSITY_HIGH:
				return 518;
			}
		} else if (code == 2) {
			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				return 518;

			case DisplayMetrics.DENSITY_MEDIUM:
				return 690;

			case DisplayMetrics.DENSITY_HIGH:
				return 1035;
			}
		}
		return 0;
	}

	private void cacheSubforums() {
		new Thread(new Runnable() {
			public void run() {
				try {
					cSite = Jsoup.connect(WEBSITE_SUPPORT).get();
					cGen = Jsoup.connect(GENERAL_DISCUSSION).get();
					cTuts = Jsoup.connect(TUTORIALS).get();
				} catch (Exception e) {
					Log.w("Caching Error - ForumHome.cacheSubforums",
							e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		cSite = new Document("");
		cGen = new Document("");
		cTuts = new Document("");

		scroll = (ScrollView) findViewById(R.id.scrollforum);

		layce3_1 = (LinearLayout) findViewById(R.id.layce3_1);
		layce3_2 = (LinearLayout) findViewById(R.id.layce3_2);
		layce3_3 = (LinearLayout) findViewById(R.id.layce3_3);
		layce2_1 = (LinearLayout) findViewById(R.id.layce2_1);
		layce2_2 = (LinearLayout) findViewById(R.id.layce2_2);
		layce2_3 = (LinearLayout) findViewById(R.id.layce2_3);
		laycom_1 = (LinearLayout) findViewById(R.id.laycom_1);
		laycom_2 = (LinearLayout) findViewById(R.id.laycom_2);
		laycom_3 = (LinearLayout) findViewById(R.id.laycom_3);

		layce3_1.setOnClickListener(this);
		layce3_2.setOnClickListener(this);
		layce3_3.setOnClickListener(this);
		layce2_1.setOnClickListener(this);
		layce2_2.setOnClickListener(this);
		layce2_3.setOnClickListener(this);
		laycom_1.setOnClickListener(this);
		laycom_2.setOnClickListener(this);
		laycom_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Utils.showLoadingPd(this);
		Intent subforum = new Intent(this, SubForum.class);
		switch (v.getId()) {
		case R.id.layce3_1:
			startActivity(new Intent(this, Ce3_1.class));
			break;
		case R.id.layce3_2:
			startActivity(new Intent(this, Ce3_2.class));
			break;
		case R.id.layce3_3:
			startActivity(new Intent(this, Ce3_3.class));
			break;
		case R.id.layce2_1:
			startActivity(new Intent(this, Ce2_1.class));
			break;
		case R.id.layce2_2:
			startActivity(new Intent(this, Ce2_2.class));
			break;
		case R.id.layce2_3:
			startActivity(new Intent(this, Ce2_3.class));
			break;
		case R.id.laycom_1:
			if (cSite.hasText()) {
				subforum.putExtra("cache", cSite.html()).putExtra("link",
						WEBSITE_SUPPORT);
			} else {
				subforum.putExtra("link", WEBSITE_SUPPORT);
			}
			startActivity(subforum);
			break;
		case R.id.laycom_2:
			if (cGen.hasText()) {
				subforum.putExtra("cache", cGen.html()).putExtra("link",
						GENERAL_DISCUSSION);
			} else {
				subforum.putExtra("link", GENERAL_DISCUSSION);
			}
			startActivity(subforum);
			break;
		case R.id.laycom_3:
			if (cTuts.hasText()) {
				subforum.putExtra("cache", cTuts.html()).putExtra("link",
						TUTORIALS);
			} else {
				subforum.putExtra("link", TUTORIALS);
			}
			startActivity(subforum);
			break;
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
