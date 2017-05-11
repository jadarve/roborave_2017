package construyelab.org.rospubsub;

import com.google.vr.sdk.widgets.common.VrWidgetView;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import geometry_msgs.Vector3;

/**
 * Created by jadarve on 8/05/17.
 */

public class YawPitchPublisher implements NodeMain {

    ///////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////

    public YawPitchPublisher(VrWidgetView videoView) {
        this.videoView = videoView;

        yawAndPitch = new float[2];
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("YawPitchPublisher");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        publisher = connectedNode.newPublisher("phone/head_orientation", "geometry_msgs/Vector3");

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {

                videoView.getHeadRotation(yawAndPitch);

                geometry_msgs.Vector3 vec = publisher.newMessage();
                vec.setX(0);        // no roll
                vec.setY(yawAndPitch[0]);
                vec.setZ(yawAndPitch[1]);

                publisher.publish(vec);

//                Log.d("construyelab", "YawPitchPublisher.loop(): " + Arrays.toString(yawAndPitch));
                Thread.sleep(20);
            }
        };

        connectedNode.executeCancellableLoop(loop);

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

    private VrWidgetView videoView;
    private float[] yawAndPitch;
    private Publisher<Vector3> publisher;
}
