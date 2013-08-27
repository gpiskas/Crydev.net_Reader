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

public class Ce3_2 extends Activity implements OnClickListener {

	private static final String PROGRAMMING_SCRIPTING = "http://www.crydev.net/viewforum.php?f=314";
	private static final String SCALEFORM = "http://www.crydev.net/viewforum.php?f=373";
	private static final String ASSET_CREATION = "http://www.crydev.net/viewforum.php?f=315";
	private static final String GENERAL_CE3_DISCUSSION = "http://www.crydev.net/viewforum.php?f=355";
	private static final String RECRUITMENT = "http://www.crydev.net/viewforum.php?f=316";

	private static Document cPs, cScale, cAsset, cGen, cRecr;

	private LinearLayout layce3_2_1, layce3_2_2, layce3_2_3, layce3_2_4,
			layce3_2_5;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.setStrictPolicy();

		setContentView(R.layout.ce3_2);
		Utils.enableActionBar(this);
		cacheSubforums();

		init();
	}

	private void cacheSubforums() {
		new Thread(new Runnable() {
			public void run() {
				try {
					cPs = Jsoup.connect(PROGRAMMING_SCRIPTING).get();
					cScale = Jsoup.connect(SCALEFORM).get();
					cAsset = Jsoup.connect(ASSET_CREATION).get();
					cGen = Jsoup.connect(GENERAL_CE3_DISCUSSION).get();
					cRecr = Jsoup.connect(RECRUITMENT).get();
				} catch (Exception e) {
					Log.w("Caching Error - Ce3_2.cacheSubforums", e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		cPs = new Document("");
		cScale = new Document("");
		cAsset = new Document("");
		cGen = new Document("");
		cRecr = new Document("");

		layce3_2_1 = (LinearLayout) findViewById(R.id.layce3_2_1);
		layce3_2_2 = (LinearLayout) findViewById(R.id.layce3_2_2);
		layce3_2_3 = (LinearLayout) findViewById(R.id.layce3_2_3);
		layce3_2_4 = (LinearLayout) findViewById(R.id.layce3_2_4);
		layce3_2_5 = (LinearLayout) findViewById(R.id.layce3_2_5);

		layce3_2_1.setOnClickListener(this);
		layce3_2_2.setOnClickListener(this);
		layce3_2_3.setOnClickListener(this);
		layce3_2_4.setOnClickListener(this);
		layce3_2_5.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Utils.showLoadingPd(this);
		Intent subforum = new Intent(this, SubForum.class);
		switch (v.getId()) {
		case R.id.layce3_2_1:
			if (cPs.hasText()) {
				subforum.putExtra("cache", cPs.html()).putExtra("link",
						PROGRAMMING_SCRIPTING);
			} else {
				subforum.putExtra("link", PROGRAMMING_SCRIPTING);
			}
			break;
		case R.id.layce3_2_2:
			if (cScale.hasText()) {
				subforum.putExtra("cache", cScale.html()).putExtra("link",
						SCALEFORM);
			} else {
				subforum.putExtra("link", SCALEFORM);
			}
			break;
		case R.id.layce3_2_3:
			if (cAsset.hasText()) {
				subforum.putExtra("cache", cAsset.html()).putExtra("link",
						ASSET_CREATION);
			} else {
				subforum.putExtra("link", ASSET_CREATION);
			}
			break;
		case R.id.layce3_2_4:
			if (cGen.hasText()) {
				subforum.putExtra("cache", cGen.html()).putExtra("link",
						GENERAL_CE3_DISCUSSION);
			} else {
				subforum.putExtra("link", GENERAL_CE3_DISCUSSION);
			}
			break;
		case R.id.layce3_2_5:
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
