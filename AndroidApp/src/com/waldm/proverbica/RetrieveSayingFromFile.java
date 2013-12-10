package com.waldm.proverbica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.res.AssetManager;

public class RetrieveSayingFromFile extends RetrieveSaying {

	private final AssetManager assets;
	private final String filename = "sayings.txt";
	private List<String> sayings;

	public RetrieveSayingFromFile(MainActivity mainActivity) {
		super(mainActivity);
		assets = mainActivity.getAssets();
	}

	@Override
	protected String doInBackground(String... files) {
		if (sayings == null) {
			sayings = new ArrayList<String>();

			InputStream stream = null;
			try {
				stream = assets.open(filename);
			} catch (IOException e) {
				e.printStackTrace();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			String saying;
			try {
				while ((saying = reader.readLine()) != null) {
					sayings.add(saying);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Random random = new Random();
		String beginning = sayings.get(random.nextInt(sayings.size())).split(
				"\\|")[0];
		String end = sayings.get(random.nextInt(sayings.size())).split("\\|")[1];

		return beginning + " " + end;
	}
}
