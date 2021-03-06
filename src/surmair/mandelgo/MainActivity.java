package surmair.mandelgo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.graphics.Bitmap;
import org.apache.commons.math.complex.Complex;

public class MainActivity extends Activity {
	private MandelRenderer renderer_a = new JavaRenderer(this);
	private MandelRenderer renderer_b = new GoRenderer(this);

	private ImageView image_a, image_b;
    private TextView output_a, output_b;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        image_a = (ImageView) findViewById(R.id.image_a);
        image_b = (ImageView) findViewById(R.id.image_b);
        output_a = (TextView) findViewById(R.id.output_a);
        output_b = (TextView) findViewById(R.id.output_b);
    }

    public void render(View view) {
    	MandelRendererParameters mrp = new MandelRendererParameters();
        mrp.width = image_a.getWidth();
        mrp.height = image_a.getHeight();
        mrp.scale = 0.005;
        mrp.colorScale = 10;
        mrp.origin = new Complex(-0.5, 0);


        renderer_a.render(mrp, new MandelRenderingCallback(){
            public void setStatus(String s) {
                String template = getString(R.string.status_update);
                output_a.setText(String.format(template, renderer_a.getName(), s));
            }

            public void setImage(Bitmap b) {
                image_a.setImageBitmap(b);
            }
        });

        renderer_b.render(mrp, new MandelRenderingCallback(){
            public void setStatus(String s) {
                String template = getString(R.string.status_update);
                output_b.setText(String.format(template, renderer_b.getName(), s));
            }

            public void setImage(Bitmap b) {
                image_b.setImageBitmap(b);
            }
        });
    }
}
