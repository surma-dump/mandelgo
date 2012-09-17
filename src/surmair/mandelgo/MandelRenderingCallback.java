package surmair.mandelgo;

import android.graphics.Bitmap;

public interface MandelRenderingCallback {
	void setStatus(String s);
	void setImage(Bitmap b);
}
