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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Prefs extends PreferenceActivity {

	private SharedPreferences sharedPrefs;
	private SharedPreferences.Editor sharedPrefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		Utils.enableActionBar(this);
		sharedPrefs = getSharedPreferences(Utils.SHARED_PREFS, 0);
		sharedPrefsEditor = sharedPrefs.edit();

		findPreference("rememberlogin").setSummary(
				getResources().getString(R.string.current_user)
						+ sharedPrefs.getString("username", ""));

		findPreference("rememberlogin").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(new Intent(Prefs.this,
								LoginDetails.class), 1);
						return true;
					}
				});

		findPreference("keepscreenon").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						if (sharedPrefs.getBoolean("keepscreenon", false) == false) {
							sharedPrefsEditor.putBoolean("keepscreenon", true);
						} else {
							sharedPrefsEditor.putBoolean("keepscreenon", false);
						}
						sharedPrefsEditor.commit();
						return true;
					}
				});
		findPreference("togglesigs").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						if (sharedPrefs.getBoolean("togglesigs", true) == true) {
							sharedPrefsEditor.putBoolean("togglesigs", false);
						} else {
							sharedPrefsEditor.putBoolean("togglesigs", true);
						}
						sharedPrefsEditor.commit();
						return true;
					}
				});

		findPreference("language").setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference arg0,
							Object arg1) {
						setResult(1337);
						finish();
						return true;
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			sharedPrefsEditor.putString("username",
					data.getExtras().getString("username"));
			sharedPrefsEditor.putString("password",
					data.getExtras().getString("password"));

			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.login_saved),
					Toast.LENGTH_SHORT).show();

		} else if (resultCode == 2) {
			sharedPrefsEditor.putString("username", "");
			sharedPrefsEditor.putString("password", "");
			sharedPrefsEditor.commit();
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.login_deleted),
					Toast.LENGTH_SHORT).show();
		}
		findPreference("rememberlogin").setSummary(
				getResources().getString(R.string.current_user)
						+ sharedPrefs.getString("username", ""));
		sharedPrefsEditor.putBoolean("userchanged", true);
		sharedPrefsEditor.commit();
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
}
