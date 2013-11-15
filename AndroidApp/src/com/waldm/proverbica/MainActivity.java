package com.waldm.proverbica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.os.AsyncTask;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("");
		textBox = (TextView) findViewById(R.id.text_box);

		image = (ImageView) findViewById(R.id.image);
		Picasso.with(this)
				.load("http://proverbica.herokuapp.com/images/lion.jpg")
				.into(image);

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new RetrieveSaying(MainActivity.this)
						.execute("http://proverbica.herokuapp.com/saying");
			}
		});
	}

	private static class RetrieveSaying extends AsyncTask<String, Void, String> {
		private MainActivity mainActivity;

		public RetrieveSaying(MainActivity mainActivity) {
			this.mainActivity = mainActivity;
		}

		@Override
		protected String doInBackground(String... urls) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(
					"http://proverbica.herokuapp.com/saying");
			HttpResponse response = null;
			try {
				response = httpClient.execute(httpGet, localContext);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String result = "";

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					result += line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mainActivity.setText(result);
		}
	}

	public void setText(String result) {
		textBox.setText(result);
		imageIndex++;
		if (imageIndex >= images.length) {
			imageIndex = 0;
		}

		Picasso.with(this)
				.load("http://proverbica.herokuapp.com/images/"
						+ images[imageIndex]).into(image);
	}
}
