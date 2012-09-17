package surmair.mandelgo;

import android.graphics.Bitmap;

public interface MandelRenderer {
	void render(MandelRendererParameters mrp, MandelRenderingCallback cb);
	String getName();
}
