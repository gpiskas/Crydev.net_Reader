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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class Ce2_1 extends Activity implements OnClickListener {

	private static final String MODS = "http://www.crydev.net/viewforum.php?f=277";
	private static final String LEVELS = "http://www.crydev.net/viewforum.php?f=278";
	private static final String ASSETS = "http://www.crydev.net/viewforum.php?f=280";
	private static final String MISC = "http://www.crydev.net/viewforum.php?f=279";

	private static Document cMods, cLevels, cAssets, cMisc;

	private LinearLayout layce2_1_1, layce2_1_2, layce2_1_3, layce2_1_4;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.setStrictPolicy();
		setContentView(R.layout.ce2_1);
		Utils.enableActionBar(this);
		cacheSubforums();

		init();
	}

	private void cacheSubforums() {
		new Thread(new Runnable() {
			public void run() {
				try {
					cMods = Jsoup.connect(MODS).get();
					cLevels = Jsoup.connect(LEVELS).get();
					cAssets = Jsoup.connect(ASSETS).get();
					cMisc = Jsoup.connect(MISC).get();
				} catch (Exception e) {
					Log.w("Caching Error - Ce2_1.cacheSubforums", e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		cMods = new Document("");
		cLevels = new Document("");
		cAssets = new Document("");
		cMisc = new Document("");

		layce2_1_1 = (LinearLayout) findViewById(R.id.layce2_1_1);
		layce2_1_2 = (LinearLayout) findViewById(R.id.layce2_1_2);
		layce2_1_3 = (LinearLayout) findViewById(R.id.layce2_1_3);
		layce2_1_4 = (LinearLayout) findViewById(R.id.layce2_1_4);

		layce2_1_1.setOnClickListener(this);
		layce2_1_2.setOnClickListener(this);
		layce2_1_3.setOnClickListener(this);
		layce2_1_4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Utils.showLoadingPd(this);
		Intent subforum = new Intent(this, SubForum.class);
		switch (v.getId()) {
		case R.id.layce2_1_1:
			if (cMods.hasText()) {
				subforum.putExtra("cache", cMods.html()).putExtra("link", MODS);
			} else {
				subforum.putExtra("link", MODS);
			}
			break;
		case R.id.layce2_1_2:
			if (cLevels.hasText()) {
				subforum.putExtra("cache", cLevels.html()).putExtra("link",
						LEVELS);
			} else {
				subforum.putExtra("link", LEVELS);
			}
			break;
		case R.id.layce2_1_3:
			if (cAssets.hasText()) {
				subforum.putExtra("cache", cAssets.html()).putExtra("link",
						ASSETS);
			} else {
				subforum.putExtra("link", ASSETS);
			}
			break;
		case R.id.layce2_1_4:
			if (cMisc.hasText()) {
				subforum.putExtra("cache", cMisc.html()).putExtra("link", MISC);
			} else {
				subforum.putExtra("link", MISC);
			}
			break;
		}
		startActivity(subforum);
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
