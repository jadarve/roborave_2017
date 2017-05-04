package construyelab.org.rospubsub;

import android.os.Bundle;
import android.util.Log;

import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import org.ros.android.view.RosTextView;


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

    }


    ///////////////////////////////////////////////////////
    // ATTRIBUTES
    ///////////////////////////////////////////////////////

    private RosTextView<std_msgs.String> rosTextView;

}
