package construyelab.org.rospubsub;

import android.os.Bundle;
import android.util.Log;

import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import org.ros.android.view.RosTextView;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.node.NodeMain;
import org.ros.node.ConnectedNode;

/**
 * Created by jadarve on 3/05/17.
 */

public class SubscriberActivity extends RosActivity {


    ///////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////

    public SubscriberActivity() {
        super("SubscriberActivity", "SubscriberActivity");
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subscriber);

        rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.rosTextView);
        rosTextView.setTopicName("roshello/greeting");
        rosTextView.setMessageType(std_msgs.String._TYPE);
        rosTextView.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {

                Log.d("construyelab", "call(): " + message.getData());

                return message.getData();
            }
        });
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

        Log.d("construyelab", "SubscriberActivity.init()");

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(rosTextView, nodeConfiguration);
//        nodeMainExecutor.execute(new NodeExample(), nodeConfiguration);

        nodeMainExecutor.execute(new OrientationPublisher(this), nodeConfiguration);

    }


    ///////////////////////////////////////////////////////
    // ATTRIBUTES
    ///////////////////////////////////////////////////////

    private RosTextView<std_msgs.String> rosTextView;

}


class NodeExample implements NodeMain {

    public NodeExample() {
        counter = 0;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("NodeExample");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        publisher = connectedNode.newPublisher("NodeExample", "std_msgs/String");

        CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {

                Log.d("construyelab", "NodeExample.onStart(): " + counter);

                std_msgs.String msg = publisher.newMessage();
                msg.setData("moni moni: " + (counter++));

                publisher.publish(msg);
                Thread.sleep(1000);
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

    private int counter;
    private Publisher<std_msgs.String> publisher;
    private Subscriber<std_msgs.String> subscriber;
}
