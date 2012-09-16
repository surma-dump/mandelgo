package surmair.mandelgo;

import android.graphics.Bitmap;

public interface MandelRenderer {
	Bitmap render(MandelRendererParameters mrp);
	String getName();
}
