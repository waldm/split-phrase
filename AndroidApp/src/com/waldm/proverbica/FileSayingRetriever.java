package com.waldm.proverbica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FileSayingRetriever implements SayingRetriever {

	private final String filename = "sayings.txt";
	private List<String> sayings;
	private final MainActivity mainActivity;
	private final ImageView imageView;

	public FileSayingRetriever(MainActivity mainActivity, ImageView imageView) {
		this.mainActivity = mainActivity;
		this.imageView = imageView;
	}

	@Override
	public void loadImage(String imageName) {
		Picasso.with(mainActivity)
				.load(mainActivity.getResources().getIdentifier(
						imageName.replace(".jpg", ""), "drawable",
						mainActivity.getPackageName())).into(imageView);
	}

	@Override
	public SayingRetriever loadSayingAndRefresh(String sayingPage) {
		if (sayings == null) {
			sayings = new ArrayList<String>();

			InputStream stream = null;
			try {
				stream = mainActivity.getAssets().open(filename);
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

		mainActivity.setText(beginning + " " + end);
		return this;
	}
}
