package com.waldm.proverbica;

import java.util.Random;

import com.waldm.proverbica.retriever.FileSayingRetriever;
import com.waldm.proverbica.retriever.SayingRetriever;
import com.waldm.proverbica.retriever.WebSayingRetriever;
import com.waldm.proverbica.settings.SettingsActivity;
import com.waldm.proverbica.settings.SettingsFragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements
		OnSharedPreferenceChangeListener {
	private TextView textBox;
	private ImageView image;
	private SayingRetriever sayingRetriever;
	private int imageIndex;
	public static final String WEBSITE = "http://proverbica.herokuapp.com/";
	private static final String SAYING_PAGE = WEBSITE + "saying";
	protected static final String[] images = { "lion.jpg", "monkey.jpg",
			"gorilla.jpg", "hawk.jpg", "owl.jpg", "dog.jpg", "tiger.jpg",
			"polar_bear.jpg", "elephant.jpg", "leopard.jpg", "cat.jpg" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle("");
		textBox = (TextView) findViewById(R.id.text_box);

		image = (ImageView) findViewById(R.id.image);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sharedPref.getBoolean(SettingsFragment.KEY_PREF_ALWAYS_USE_FILE,
				false)) {
			sayingRetriever = new FileSayingRetriever(this, image);
		} else {
			sayingRetriever = new WebSayingRetriever(this, image);
		}

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sayingRetriever = sayingRetriever
						.loadSayingAndRefresh(SAYING_PAGE);
			}
		});

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, textBox.getText()
						+ " - www.proverbica.com");
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent,
						getString(R.string.share_proverb)));
			}
		});

		sayingRetriever = sayingRetriever.loadSayingAndRefresh(SAYING_PAGE);
	}

	public void setText(String result) {
		textBox.setText(result);
		generateNextImageIndex();
		sayingRetriever.loadImage(images[imageIndex]);
	}

	private void generateNextImageIndex() {
		int newImageIndex = new Random().nextInt(images.length);
		while (newImageIndex == imageIndex) {
			newImageIndex = new Random().nextInt(images.length);
		}

		imageIndex = newImageIndex;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(SettingsFragment.KEY_PREF_ALWAYS_USE_FILE)) {
			if (sharedPreferences.getBoolean(key, false)) {
				sayingRetriever = new FileSayingRetriever(this, image);
			} else {
				sayingRetriever = new WebSayingRetriever(this, image);
			}
		}
	}
}
