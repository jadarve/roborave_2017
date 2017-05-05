package construyelab.org.rospubsub;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.util.Arrays;

/**
 * Created by jadarve on 5/05/17.
 */

public class OrientationPublisher implements NodeMain, SensorEventListener {


    public OrientationPublisher(Activity activity) {

        this.activity = activity;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        Log.d("construyelab", "OrientationPublisher.onSensorChanged(): " + Arrays.toString(event.values));

        geometry_msgs.QuaternionStamped q = publisher.newMessage();
        q.getHeader().setStamp(Time.fromNano(event.timestamp));

        q.getQuaternion().setX(event.values[0]);
        q.getQuaternion().setY(event.values[1]);
        q.getQuaternion().setZ(event.values[2]);
        q.getQuaternion().setW(event.values[3]);

        publisher.publish(q);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("OrientationPublisher");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        publisher = connectedNode.newPublisher("phone/orientation", "geometry_msgs/QuaternionStamped");


        sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onShutdown(Node node) {

    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {
        Log.e(node.getName().toString(), "OrientationPublisher.onError(): " + throwable.getMessage());
    }


    private Activity activity;

    private SensorManager sensorManager;
    private Sensor orientationSensor;

    private Publisher<geometry_msgs.QuaternionStamped> publisher;


}
