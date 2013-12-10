package com.waldm.proverbica;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView textBox;
	private ImageView image;
	private RetrieveSaying retrieveSaying;
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
		retrieveSaying = new RetrieveSayingFromFile(this, image);

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				retrieveSaying.loadSaying(SAYING_PAGE);
			}
		});

		retrieveSaying.loadSaying(SAYING_PAGE);
	}

	public void setText(String result) {
		textBox.setText(result);
		generateNextImageIndex();
		retrieveSaying.loadImage(images[imageIndex]);
	}

	private void generateNextImageIndex() {
		int newImageIndex = new Random().nextInt(images.length);
		while (newImageIndex == imageIndex) {
			newImageIndex = new Random().nextInt(images.length);
		}

		imageIndex = newImageIndex;
	}
}
