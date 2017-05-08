package construyelab.org.rospubsub;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.video.VrVideoView;

import org.ros.android.RosActivity;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.io.IOException;

import geometry_msgs.Vector3;
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

        setContentView(R.layout.activity_vr);
        imageView = (VrPanoramaView)findViewById(R.id.VrImageView);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageView.resumeRendering();
    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(new YawPitchPublisher(imageView), nodeConfiguration);

    }

    private VrPanoramaView imageView;
}

class ImageSubscriber implements NodeMain, MessageListener<sensor_msgs.CompressedImage> {


    public ImageSubscriber(VrPanoramaView imageView) {
        this.imageView = imageView;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("VrImageActivity");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        Subscriber<sensor_msgs.CompressedImage> subscriber = connectedNode.newSubscriber(
                "camera/image_compressed", "sensor_msgs.CompressedImage");

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

        Log.d("construyelab", "ImageSubscriber.onNewMessage():");

        Bitmap bitmap = BitmapFactory.decodeByteArray(compressedImage.getData().array(),
                0, compressedImage.getData().array().length);

        VrPanoramaView.Options opt = new VrPanoramaView.Options();
        opt.inputType = VrPanoramaView.Options.TYPE_MONO;
        imageView.loadImageFromBitmap(bitmap, opt);
    }


    private VrPanoramaView imageView;
}