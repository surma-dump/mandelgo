package surmair.mandelgo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.content.Context;
import org.apache.commons.math.complex.Complex;

public class JavaRenderer implements MandelRenderer {
	private static final int ITERATION_LIMIT = 100;
	private Context ctx;

	public JavaRenderer(Context ctx) {
		this.ctx = ctx;
	}

	public void render(MandelRendererParameters mrp, MandelRenderingCallback cb) {
		new JavaImageLoader().execute(mrp, cb);
	}

	public String getName() {
		return "JavaRenderer";
	}

	private class JavaImageLoader extends TimedImageLoader {
		@Override
		protected Bitmap doInBackground(Object... params) {
			cb = (MandelRenderingCallback)params[1];
			MandelRendererParameters mrp = (MandelRendererParameters)params[0];
			Bitmap b = Bitmap.createBitmap(mrp.width, mrp.height, Bitmap.Config.ARGB_8888);
			Remapper map_x = new Remapper(0, (double)mrp.width, -mrp.scale*((double)mrp.width)/2, mrp.scale*((double)mrp.width)/2);
			Remapper map_y = new Remapper(0, (double)mrp.height, -mrp.scale*((double)mrp.height)/2, mrp.scale*((double)mrp.height)/2);
			String template = ctx.getString(R.string.progress_update);

			float[] hsv = new float[]{0.0f, 1.0f, 1.0f};
			tick();
			for(int pixel_y = 0; pixel_y < mrp.height; pixel_y++) {
				for(int pixel_x = 0; pixel_x < mrp.width; pixel_x++) {
					double real = mrp.origin.getReal() + map_x.map(pixel_x);
					double imag = mrp.origin.getImaginary() + map_y.map(pixel_y);
					Complex z = new Complex(real, imag);
					Complex c = new Complex(real, imag);
					int i = 0;
					while(i < ITERATION_LIMIT && z.abs() <= 2) {
						z = z.multiply(z).add(c);
						i++;
					}
					int col;
					if(z.abs() <= 2) {
						col = Color.BLACK;
					} else {
						float h = (float)i/ITERATION_LIMIT * 360.0f * (float)mrp.colorScale;
						while(h > 360.0) {
							h -= 360.0;
						}
						hsv[0] = h;
						col = Color.HSVToColor(hsv);
					}
					b.setPixel(pixel_x, pixel_y, col);
				}
				publishProgress(String.format(template, (double)pixel_y*100.0/(double)mrp.height));
			}
			tock();
			return b;
		}
	}
}
