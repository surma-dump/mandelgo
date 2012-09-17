package surmair.mandelgo;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public abstract class TimedImageLoader extends AsyncTask<Object, String, Bitmap> {
	protected MandelRenderingCallback cb;
	protected long start, duration;


	protected void tick() {
		start = System.nanoTime();
	}

	protected void tock() {
		duration = System.nanoTime() - start;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		String update = values[0];
		cb.setStatus(update);
	}

	@Override
	protected void onPostExecute(Bitmap b) {
		cb.setStatus(String.format("%.3f", duration/1000000.0));
		if(b != null) {
			cb.setImage(b);
		}
	}
}

