package construyelab.org.rospubsub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.video.VrVideoView;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.RosActivity;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;

import sensor_msgs.CompressedImage;

/**
 * Created by jadarve on 8/05/17.
 */

public class VrImageActivity extends RosActivity {


    ///////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////

    public VrImageActivity() {
        super("VrImageActivity", "VrImageActivity");
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_vr_image);
        imageView = (VrPanoramaView)findViewById(R.id.VrImageView);
        imageView.setVisibility(View.INVISIBLE);

        imageLeft = (ImageView)findViewById(R.id.ImageLeft);
        imageRight = (ImageView)findViewById(R.id.ImageRight);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageView.resumeRendering();

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        Log.d("construyelab", "window size: [" + metrics.heightPixels + ", " +
                metrics.widthPixels + "]");

        Log.d("construyelab", "image size: [" + imageLeft.getHeight() + ", " +
                imageLeft.getWidth() + "]");
    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(new YawPitchPublisher(imageView), nodeConfiguration);
        nodeMainExecutor.execute(new ImageSubscriber(imageView, imageLeft, imageRight), nodeConfiguration);

    }

    private VrPanoramaView imageView;
    private ImageView imageLeft;
    private ImageView imageRight;
}

class ImageSubscriber implements NodeMain, MessageListener<sensor_msgs.CompressedImage> {


    public ImageSubscriber(VrPanoramaView imageView, ImageView imageLeft, ImageView imageRight) {
        this.imageView = imageView;
        this.imageLeft = imageLeft;
        this.imageRight = imageRight;
        decoder = new BitmapFromCompressedImage();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("VrImageActivity");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        Subscriber<sensor_msgs.CompressedImage> subscriber = connectedNode.newSubscriber(
                "camera/image_color/compressed", "sensor_msgs/CompressedImage");

        subscriber.addMessageListener(this);
    }

    @Override
    public void onShutdown(Node node) {

    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {

    }

    @Override
    public void onNewMessage(CompressedImage compressedImage) {

//        Log.d("construyelab", "ImageSubscriber.onNewMessage():");

//        Bitmap bitmap = BitmapFactory.decodeByteArray(compressedImage.getData().array(),
//                0, compressedImage.getData().array().length);


        final Bitmap bitmap = decoder.call(compressedImage);

//        Log.d("construyelab", "ImageSubscriber.onNewMessage(): " + compressedImage.getData().array().length);

//        int height = bitmap.getHeight();
//        int width = bitmap.getWidth();
//        Log.d("construyelab", "ImageSubscriber.onNewMessage(): [" + height + " , " + width + "]");

//        VrPanoramaView.Options opt = new VrPanoramaView.Options();
//        opt.inputType = VrPanoramaView.Options.TYPE_MONO;
//        imageView.loadImageFromBitmap(bitmap, opt);

        imageLeft.post(new Runnable() {
            @Override
            public void run() {
                imageLeft.setImageBitmap(bitmap);
            }
        });

        imageRight.post(new Runnable() {
            @Override
            public void run() {
                imageRight.setImageBitmap(bitmap);
            }
        });

    }

    BitmapFromCompressedImage decoder;

    private VrPanoramaView imageView;
    private ImageView imageLeft;
    private ImageView imageRight;
}