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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginDetails extends Activity implements OnClickListener {

	private SharedPreferences sharedPrefs;
	private Button bSave, bCancel, bClear;
	private EditText etUsername, etPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logindetails);
		sharedPrefs = getSharedPreferences(Utils.SHARED_PREFS, 0);
		init();
	}

	private void init() {
		etUsername = (EditText) findViewById(R.id.etusername);
		etPassword = (EditText) findViewById(R.id.etpassword);
		if (!sharedPrefs.getString("username", "").equals("")) {
			etUsername.setText(sharedPrefs.getString("username", ""));
			etPassword.setText(sharedPrefs.getString("password", ""));
		}
		bSave = (Button) findViewById(R.id.bsave);
		bCancel = (Button) findViewById(R.id.bcancel);
		bClear = (Button) findViewById(R.id.bclear);

		bSave.setOnClickListener(this);
		bCancel.setOnClickListener(this);
		bClear.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent returning = new Intent();
		switch (v.getId()) {
		case R.id.bsave:
			if (!etUsername.getText().toString().trim().equals("")
					&& !etPassword.getText().toString().trim().equals("")) {

				returning.putExtra("username", etUsername.getText().toString());
				returning.putExtra("password", etPassword.getText().toString());
				setResult(1, returning);
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.fill_fields),
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.bclear:
			etUsername.setText("");
			etPassword.setText("");
			setResult(2, returning);
			finish();
			break;
		case R.id.bcancel:
			setResult(0, returning);
			finish();
			break;
		}
	}
}
