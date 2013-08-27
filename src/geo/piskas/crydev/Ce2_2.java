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

public class Ce2_2 extends Activity implements OnClickListener {

	private static final String BEGINNER_QUESTIONS = "http://www.crydev.net/viewforum.php?f=282";
	private static final String PROGRAMMING_SCRIPTING = "http://www.crydev.net/viewforum.php?f=283";
	private static final String ASSET_CREATION = "http://www.crydev.net/viewforum.php?f=284";
	private static final String RECRUITMENT = "http://www.crydev.net/viewforum.php?f=290";

	private static Document cBegin, cPs, cAsset, cRecr;

	private LinearLayout layce2_2_1, layce2_2_2, layce2_2_3, layce2_2_4;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.setStrictPolicy();
		setContentView(R.layout.ce2_2);
		Utils.enableActionBar(this);
		cacheSubforums();

		init();
	}

	private void cacheSubforums() {
		new Thread(new Runnable() {
			public void run() {
				try {
					cBegin = Jsoup.connect(BEGINNER_QUESTIONS).get();
					cPs = Jsoup.connect(PROGRAMMING_SCRIPTING).get();
					cAsset = Jsoup.connect(ASSET_CREATION).get();
					cRecr = Jsoup.connect(RECRUITMENT).get();
				} catch (Exception e) {
					Log.w("Caching Error - Ce2_2.cacheSubforums", e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		cBegin = new Document("");
		cPs = new Document("");
		cAsset = new Document("");
		cRecr = new Document("");

		layce2_2_1 = (LinearLayout) findViewById(R.id.layce2_2_1);
		layce2_2_2 = (LinearLayout) findViewById(R.id.layce2_2_2);
		layce2_2_3 = (LinearLayout) findViewById(R.id.layce2_2_3);
		layce2_2_4 = (LinearLayout) findViewById(R.id.layce2_2_4);

		layce2_2_1.setOnClickListener(this);
		layce2_2_2.setOnClickListener(this);
		layce2_2_3.setOnClickListener(this);
		layce2_2_4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Utils.showLoadingPd(this);
		Intent subforum = new Intent(this, SubForum.class);
		switch (v.getId()) {
		case R.id.layce2_2_1:
			if (cBegin.hasText()) {
				subforum.putExtra("cache", cBegin.html()).putExtra("link",
						BEGINNER_QUESTIONS);
			} else {
				subforum.putExtra("link", BEGINNER_QUESTIONS);
			}
			break;
		case R.id.layce2_2_2:
			if (cPs.hasText()) {
				subforum.putExtra("cache", cPs.html()).putExtra("link",
						PROGRAMMING_SCRIPTING);
			} else {
				subforum.putExtra("link", PROGRAMMING_SCRIPTING);
			}
			break;
		case R.id.layce2_2_3:
			if (cAsset.hasText()) {
				subforum.putExtra("cache", cAsset.html()).putExtra("link",
						ASSET_CREATION);
			} else {
				subforum.putExtra("link", ASSET_CREATION);
			}
			break;
		case R.id.layce2_2_4:
			if (cRecr.hasText()) {
				subforum.putExtra("cache", cRecr.html()).putExtra("link",
						RECRUITMENT);
			} else {
				subforum.putExtra("link", RECRUITMENT);
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
