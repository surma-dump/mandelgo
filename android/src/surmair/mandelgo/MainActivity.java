package surmair.mandelgo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.graphics.Bitmap;
import org.apache.commons.math.complex.Complex;

public class MainActivity extends Activity implements MandelRenderingCallback {
	private MandelRenderer renderer_a = new JavaRenderer();
	private MandelRenderer renderer_b = new GoRenderer();
    private MandelRenderer current_renderer;

	private ImageView image_a, image_b, current_image;
    private TextView output_a, output_b, current_output;
    private long start;
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


        current_image = image_a;
        current_output = output_a;
        current_renderer = renderer_a;
        current_output.setText(getString(R.string.working));
        start = System.nanoTime();
        current_renderer.render(mrp, this);

        current_image = image_b;
        current_output = output_b;
        current_renderer = renderer_b;
        current_output.setText(getString(R.string.working));
        start = System.nanoTime();
        current_renderer.render(mrp, this);
    }

    public void setImage(Bitmap b) {
        current_image.setImageBitmap(b);
        String template = getString(R.string.benchmark_result);
        long duration = System.nanoTime() - start;
        current_output.setText(String.format(template, current_renderer.getName(), duration/1000000.0));
    }
}
