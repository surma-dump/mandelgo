package surmair.mandelgo;

import android.graphics.Bitmap;
import android.graphics.Color;

public class DummyRenderer implements MandelRenderer {
	public void render(MandelRendererParameters mrp, MandelRenderingCallback cb) {
		Bitmap b = Bitmap.createBitmap(mrp.width, mrp.height, Bitmap.Config.ARGB_8888);
		for(int y = 0; y < mrp.height; y++) {
			b.setPixel(0, y, Color.RED);
			b.setPixel(mrp.width-1, y, Color.RED);
		}
		for(int x = 0; x < mrp.width; x++) {
			b.setPixel(x, 0, Color.RED);
			b.setPixel(x, mrp.height-1, Color.RED);
		}
		cb.setImage(b);
	}

	public String getName() {
		return "DummyRenderer";
	}
}
