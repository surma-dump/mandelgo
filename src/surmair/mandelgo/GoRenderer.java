package surmair.mandelgo;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;

public class GoRenderer implements MandelRenderer {
	private static final String address = "http://localhost:8080/render_image?width=%d&height=%d&origin=%f,%f&scale=%f&colorscale=%f";
	private MandelRenderingCallback cb = null;
	public void render(MandelRendererParameters mrp, MandelRenderingCallback cb) {
		String url = String.format(address, mrp.width, mrp.height, mrp.origin.getReal(), mrp.origin.getImaginary(), mrp.scale, mrp.colorScale);
		this.cb = cb;
		new ImageLoader().execute(url);
	}

	public String getName() {
		return "GoRenderer";
	}

	private class ImageLoader extends AsyncTask<String, Void, Bitmap>{
		@Override
		protected Bitmap doInBackground(String... urls) {
			InputStream is = null;
			Bitmap b = null;
			// params comes from the execute() call: params[0] is the url.
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection huc = (HttpURLConnection) url.openConnection();
				huc.setReadTimeout(10000 /* milliseconds */);
				huc.setRequestMethod("GET");
				huc.setDoInput(true);
				huc.connect();
				is = huc.getInputStream();
				b = BitmapFactory.decodeStream(is);
				// So sew me. Chicken'n'Egg problem.
				is.close();
			} catch (IOException e) {
			}

			return b;
		}

		@Override
		protected void onPostExecute(Bitmap b) {
			if(b != null) {
				cb.setImage(b);
			}
		}
	}
}

