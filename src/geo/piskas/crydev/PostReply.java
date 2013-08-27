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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class PostReply extends Activity {

	private WebView webview;

	private SharedPreferences sharedPrefs;
	private SharedPreferences.Editor sharedPrefsEditor;

	private String username, password;
	private Button bPostReply, bChangeUser;
	private EditText etPost, etUsername, etPassword;
	private ProgressBar progress;

	private boolean userchanged, loggedin, posted, backButtonEnabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postreply);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		webview = new WebView(this);
		init();
		prepareWebview();
	}

	private void init() {
		sharedPrefs = getSharedPreferences(Utils.SHARED_PREFS, 0);

		bPostReply = (Button) findViewById(R.id.bpostreply);
		bChangeUser = (Button) findViewById(R.id.bchangeuser);
		etPost = (EditText) findViewById(R.id.etpost);
		etUsername = (EditText) findViewById(R.id.etpostusername);
		etPassword = (EditText) findViewById(R.id.etpostpassword);
		progress = (ProgressBar) findViewById(R.id.progresspost);
		try {
			etPost.setText(getIntent().getStringExtra("backup"));
		} catch (Exception e) {

		}
		username = sharedPrefs.getString("username", "");
		etUsername.setText(username);
		password = sharedPrefs.getString("password", "");
		etPassword.setText(password);

		userchanged = sharedPrefs.getBoolean("userchanged", false);
		sharedPrefsEditor = sharedPrefs.edit();
		sharedPrefsEditor.putBoolean("userchanged", false);
		sharedPrefsEditor.commit();

		loggedin = false;
		posted = false;
		backButtonEnabled = true;

		if (username.equals("")) {
			bChangeUser.setVisibility(View.GONE);
			etUsername.setEnabled(true);
			etPassword.setEnabled(true);
			userchanged = true;
		}
	}

	private void prepareWebview() {
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setSavePassword(false);
		webview.setWebChromeClient(new WebChromeClient());
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {

				if (webview.getTitle().contains("Information")
						&& webview
								.findAll("This message has been posted successfully.") > 0) {

					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.reply_posted),
							Toast.LENGTH_LONG).show();
					webview.stopLoading();
					setResult(1);
					finish();
				}

				if (userchanged && webview.getTitle().contains("Post a reply")) {
					webview.loadUrl("javascript:(function() { window.location = document.getElementById('login').href })()");
					Handler myHandler = new Handler();
					myHandler.postDelayed(loadPostPage, 3000);
					userchanged = false;
				} else if (webview.getTitle().contains("Post a reply")) {
					if (!posted) {
						webview.loadUrl("javascript:(function() { document.getElementsByName('subject')[0].value='Posted using Crydev Reader!'; document.getElementsByName('message')[0].value ='"
								+ etPost.getText().toString()
								+ "'; document.getElementsByName('post')[0].click(); })()");
						posted = true;
					}
				} else if (webview.getTitle().contains("Log in")) {
					if (loggedin) {
						webview.stopLoading();
						progress.setVisibility(View.INVISIBLE);
						Utils.showIncorrectLoginAndRestart(PostReply.this,
								etPost.getText().toString());
					} else {
						webview.loadUrl("javascript:(function() { document.getElementsByName('username')[1].value='"
								+ username
								+ "'; document.getElementsByName('password')[1].value='"
								+ password
								+ "'; document.getElementsByName('login')[1].click(); })()");
						loggedin = true;
						userchanged = false;
						sharedPrefsEditor = sharedPrefs.edit();
						sharedPrefsEditor.putString("username", username);
						sharedPrefsEditor.putString("password", password);
						sharedPrefsEditor.commit();
					}
				}
			}
		});

	}

	public void changeUserClick(View v) {
		bChangeUser.setVisibility(View.GONE);
		etUsername.setText("");
		etPassword.setText("");
		etUsername.setEnabled(true);
		etPassword.setEnabled(true);
		userchanged = true;
	}

	public void postReplyClick(View v) {
		if (!etUsername.getText().toString().trim().equals("")
				&& !etPassword.getText().toString().trim().equals("")
				&& !etPost.getText().toString().trim().equals("")) {
			bPostReply.setVisibility(View.GONE);
			bChangeUser.setEnabled(false);
			progress.setVisibility(View.VISIBLE);
			etPost.setEnabled(false);
			etUsername.setEnabled(false);
			etPassword.setEnabled(false);
			backButtonEnabled = false;
			try {
				username = etUsername.getText().toString().trim();
				password = etPassword.getText().toString().trim();
				webview.loadUrl(getIntent().getStringExtra("link"));

			} catch (Exception e) {
				Utils.showConnectionErrorPopup(webview, this);
				Log.e("Connection Error - PostReply", e.toString());
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.fill_fields),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onBackPressed() {
		if (backButtonEnabled) {
			super.onBackPressed();
		}
	}

	private Runnable loadPostPage = new Runnable() {
		@Override
		public void run() {
			webview.loadUrl(getIntent().getStringExtra("link"));
		}
	};

}
