package com.waldm.proverbica;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {
	private TextView textBox;
	private static final String[] images = { "lion.jpg", "monkey.jpg",
			"gorilla.jpg", "hawk.jpg", "owl.jpg", "dog.jpg", "tiger.jpg",
			"polar_bear.jpg", "elephant.jpg", "leopard.jpg", "cat.jpg" };
	private int imageIndex = 0;
	private ImageView image;
	private RetrieveSaying retrieveSaying;
	private static final String WEBSITE = "http://proverbica.herokuapp.com/";
	private static final String SAYING_PAGE = WEBSITE + "saying";
	private static final String IMAGES_DIR = WEBSITE + "images/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle("");
		textBox = (TextView) findViewById(R.id.text_box);

		image = (ImageView) findViewById(R.id.image);
		Picasso.with(this)
				.load(IMAGES_DIR + images[new Random().nextInt(images.length)])
				.into(image);

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				retrieveSaying = new RetrieveSayingFromFile(MainActivity.this);
				retrieveSaying.execute(SAYING_PAGE);
			}
		});

		retrieveSaying = new RetrieveSayingFromFile(this);
		retrieveSaying.execute(SAYING_PAGE);
	}

	public void setText(String result) {
		textBox.setText(result);
		int newImageIndex = new Random().nextInt(images.length);
		while (newImageIndex == imageIndex) {
			newImageIndex = new Random().nextInt(images.length);
		}

		imageIndex = newImageIndex;

		Picasso.with(this).load(IMAGES_DIR + images[imageIndex]).into(image);
	}
}
