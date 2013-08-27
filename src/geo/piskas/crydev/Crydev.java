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

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Crydev extends Activity implements OnClickListener {

	private Button bNews, bForum, bBookmarks, bPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeLanguage();
		setContentView(R.layout.crydev);

		if (Utils.connectedToInternet(this)) {
			init();
		} else {
			showAlert();
		}

	}

	private void changeLanguage() {
		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		Locale locale = new Locale(getPrefs.getString("language", "en"));
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, null);
	}

	private void showAlert() {
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
				System.exit(0);
			}
		});

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources()
				.getString(R.string.exit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				});
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources()
				.getString(R.string.retry),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						if (Utils.connectedToInternet(Crydev.this)) {
							init();
							Toast.makeText(
									Crydev.this,
									getResources()
											.getString(R.string.connected),
									Toast.LENGTH_SHORT).show();
						} else {
							showAlert();
						}
					}
				});
		alertDialog.show();
	}

	private void init() {
		bNews = (Button) findViewById(R.id.bnews);
		bForum = (Button) findViewById(R.id.bforum);
		bBookmarks = (Button) findViewById(R.id.bbookmarks);
		bPrefs = (Button) findViewById(R.id.bprefs);

		bNews.setOnClickListener(this);
		bForum.setOnClickListener(this);
		bBookmarks.setOnClickListener(this);
		bPrefs.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		Utils.dismissLoadingPd();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		Utils.showLoadingPd(this);
		switch (v.getId()) {
		case R.id.bnews:
			startActivity(new Intent(this, NewsHome.class));
			break;
		case R.id.bforum:
			startActivity(new Intent(this, ForumHome.class));
			break;
		case R.id.bbookmarks:
			startActivity(new Intent(this, Bookmarks.class));
			break;
		case R.id.bprefs:
			startActivityForResult(new Intent(this, Prefs.class), 1337);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1337) {
			Intent i = getIntent();
			startActivity(i);
			System.exit(0);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menumain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.pref_app_thread) {
			startActivity(new Intent(this, ForumThread.class).putExtra("link",
					"http://www.crydev.net/viewtopic.php?f=126&t=94133"));
		} else if (item.getItemId() == R.id.pref_about) {
			startActivity(new Intent(this, About.class));
		}
		return true;
	}
}
