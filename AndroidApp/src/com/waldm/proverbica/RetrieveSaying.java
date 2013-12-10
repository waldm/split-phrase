package com.waldm.proverbica;

import android.os.AsyncTask;

public abstract class RetrieveSaying extends AsyncTask<String, Void, String> {
	private final MainActivity mainActivity;

	public RetrieveSaying(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mainActivity.setText(result);
	}
}
