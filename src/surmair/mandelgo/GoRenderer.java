package surmair.mandelgo;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.content.Context;

public class GoRenderer implements MandelRenderer {
	private Context ctx;
	private static final String address = "http://localhost:8080/render_image?width=%d&height=%d&origin=%f,%f&scale=%f&colorscale=%f";

	public GoRenderer(Context ctx) {
		this.ctx = ctx;
	}

	public void render(MandelRendererParameters mrp, MandelRenderingCallback cb) {
		String url = String.format(address, mrp.width, mrp.height, mrp.origin.getReal(), mrp.origin.getImaginary(), mrp.scale, mrp.colorScale);
		new GoImageLoader().execute(url, cb);
	}

	public String getName() {
		return "GoRenderer";
	}

	private class GoImageLoader extends TimedImageLoader {
		@Override
		protected Bitmap doInBackground(Object... params) {
			cb = (MandelRenderingCallback)params[1];
			String url = (String)params[0];
			Bitmap b = null;
			try {
				tick();
				b = getBitmapFromURL(url);
				tock();
			} catch(IOException e) {
				publishProgress(String.format("Failure: %s", e.toString()));
			}
			return b;
		}

		private Bitmap getBitmapFromURL(String _url) throws IOException {
			Bitmap b = null;
			InputStream is = null;
			try {
				publishProgress(ctx.getString(R.string.contacting_server));
				URL url = new URL(_url);
				HttpURLConnection huc = (HttpURLConnection) url.openConnection();
				huc.setReadTimeout(10000 /* milliseconds */);
				huc.setRequestMethod("GET");
				huc.setDoInput(true);
				publishProgress(ctx.getString(R.string.decoding_stream));
				huc.connect();
				is = huc.getInputStream();
				b = BitmapFactory.decodeStream(is);
			} catch (IOException e) {
				throw e;
			} finally {
				if(is != null) {
					is.close();
				}
			}
			return b;
		}
	}
}

