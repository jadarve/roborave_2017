package construyelab.org.rospubsub;

import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.widgets.video.VrVideoView;

import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import std_msgs.String;

/**
 * Created by juan on 5/7/17.
 */

public class VRActivity extends RosActivity {


    ///////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////

    public VRActivity() {
        super("VRActivity", "VRActivity");

//        float[] headRotation = new float[2];
//        videoView.getHeadRotation(headRotation);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vr);

        videoView = (VrVideoView)findViewById(R.id.VRVideoView);
    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());

//        nodeMainExecutor.execute(new YawPitchPublisher(), nodeConfiguration);



    }

    com.google.vr.sdk.widgets.video.VrVideoView videoView;
}

class YawPitchPublisher implements NodeMain {

    ///////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////

    public YawPitchPublisher(VrVideoView videoView) {

    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("YawPitchPublisher");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        publisher = connectedNode.newPublisher("phone/head_orientation", "geometry_msgs/Vector3");


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

    private VrVideoView videoView;
    private Publisher<geometry_msgs.Vector3> publisher;
}