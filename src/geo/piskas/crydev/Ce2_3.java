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

public class Ce2_3 extends Activity implements OnClickListener {

	private static final String LEVEL_DESIGN = "http://www.crydev.net/viewforum.php?f=287";
	private static final String GAME_DESIGN = "http://www.crydev.net/viewforum.php?f=288";
	private static final String ANIMATION = "http://www.crydev.net/viewforum.php?f=289";
	private static final String AI_SMART = "http://www.crydev.net/viewforum.php?f=318";
	private static final String FLOWGRAPH = "http://www.crydev.net/viewforum.php?f=319";
	private static final String TOOLS_SUPPORT = "http://www.crydev.net/viewforum.php?f=285";

	private static Document cLevel, cGame, cAnim, cAi, cFlow, cTools;

	private LinearLayout layce2_3_1, layce2_3_2, layce2_3_3, layce2_3_4,
			layce2_3_5, layce2_3_6;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.setStrictPolicy();

		setContentView(R.layout.ce2_3);
		Utils.enableActionBar(this);
		cacheSubforums();

		init();
	}

	private void cacheSubforums() {
		new Thread(new Runnable() {
			public void run() {
				try {
					cLevel = Jsoup.connect(LEVEL_DESIGN).get();
					cGame = Jsoup.connect(GAME_DESIGN).get();
					cAnim = Jsoup.connect(ANIMATION).get();
					cAi = Jsoup.connect(AI_SMART).get();
					cFlow = Jsoup.connect(FLOWGRAPH).get();
					cTools = Jsoup.connect(TOOLS_SUPPORT).get();
				} catch (Exception e) {
					Log.w("Caching Error - Ce2_3.cacheSubforums", e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		cLevel = new Document("");
		cGame = new Document("");
		cAnim = new Document("");
		cAi = new Document("");
		cFlow = new Document("");
		cTools = new Document("");

		layce2_3_1 = (LinearLayout) findViewById(R.id.layce2_3_1);
		layce2_3_2 = (LinearLayout) findViewById(R.id.layce2_3_2);
		layce2_3_3 = (LinearLayout) findViewById(R.id.layce2_3_3);
		layce2_3_4 = (LinearLayout) findViewById(R.id.layce2_3_4);
		layce2_3_5 = (LinearLayout) findViewById(R.id.layce2_3_5);
		layce2_3_6 = (LinearLayout) findViewById(R.id.layce2_3_6);

		layce2_3_1.setOnClickListener(this);
		layce2_3_2.setOnClickListener(this);
		layce2_3_3.setOnClickListener(this);
		layce2_3_4.setOnClickListener(this);
		layce2_3_5.setOnClickListener(this);
		layce2_3_6.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Utils.showLoadingPd(this);
		Intent subforum = new Intent(this, SubForum.class);
		switch (v.getId()) {
		case R.id.layce2_3_1:
			if (cLevel.hasText()) {
				subforum.putExtra("cache", cLevel.html()).putExtra("link",
						LEVEL_DESIGN);
			} else {
				subforum.putExtra("link", LEVEL_DESIGN);
			}
			break;
		case R.id.layce2_3_2:
			if (cGame.hasText()) {
				subforum.putExtra("cache", cGame.html()).putExtra("link",
						GAME_DESIGN);
			} else {
				subforum.putExtra("link", GAME_DESIGN);
			}
			break;
		case R.id.layce2_3_3:
			if (cAnim.hasText()) {
				subforum.putExtra("cache", cAnim.html()).putExtra("link",
						ANIMATION);
			} else {
				subforum.putExtra("link", ANIMATION);
			}
			break;
		case R.id.layce2_3_4:
			if (cAi.hasText()) {
				subforum.putExtra("cache", cAi.html()).putExtra("link",
						AI_SMART);
			} else {
				subforum.putExtra("link", AI_SMART);
			}
			break;
		case R.id.layce2_3_5:
			if (cFlow.hasText()) {
				subforum.putExtra("cache", cFlow.html()).putExtra("link",
						FLOWGRAPH);
			} else {
				subforum.putExtra("link", FLOWGRAPH);
			}
			break;
		case R.id.layce2_3_6:
			if (cTools.hasText()) {
				subforum.putExtra("cache", cTools.html()).putExtra("link",
						TOOLS_SUPPORT);
			} else {
				subforum.putExtra("link", TOOLS_SUPPORT);
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
