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
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.vr.sdk.base.GvrView;
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
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import sensor_msgs.CompressedImage;

/**
 * Created by jadarve on 8/05/17.
 */

public class VrImageActivity extends RosActivity implements View.OnClickListener {


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

        vrImageActivityLayout = (LinearLayout)findViewById(R.id.VrImageActivityLayout);

        imageView = (VrPanoramaView)findViewById(R.id.VrImageView);
        imageView.setVisibility(View.INVISIBLE);

        imageLeft = (ImageView)findViewById(R.id.ImageLeft);
        imageRight = (ImageView)findViewById(R.id.ImageRight);

        imageLeft.setOnClickListener(this);
        imageRight.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

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

        yawPitchPublisher = new YawPitchPublisher(imageView);

        nodeMainExecutor.execute(yawPitchPublisher, nodeConfiguration);
        nodeMainExecutor.execute(new ImageSubscriber(imageView, imageLeft, imageRight), nodeConfiguration);

        joystickPublisher = new JoystickPublisher();
        nodeMainExecutor.execute(joystickPublisher, nodeConfiguration);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
//                showlog("onKeyDown: " + keyCode);
//                switch (keyCode) {
//                    // Handle gamepad and D-pad button presses to
//                    // navigate the ship
//
//                    default:
//                        if (isFireKey(keyCode)) {
//                            // Update the ship object to fire lasers
//                            handled = true;
//                        }
//                        break;
//                }
            }
            if (handled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }

        return super.onGenericMotionEvent(event);
    }

    @Override
    public void onClick(View v) {

//        vrImageActivityLayout.removeView(imageView);
//
//        // instantiate a new image view with default head orientation
//        imageView = new VrPanoramaView(getApplicationContext());
//        imageView.setLayoutParams(new LinearLayout.LayoutParams(1, 1));
//        vrImageActivityLayout.addView(imageView);
//
//
//
//        yawPitchPublisher.setView(imageView);
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {

        InputDevice mInputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }
        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
        }

        // Update the ship object based on the new x and y values
        Log.i("CONTROL", "processJoystickInput x: " + x);
        Log.i("CONTROL", "processJoystickInput y: " + y);

        joystickPublisher.publishMessage(200 * -y, 200 * x);


//        FIXME: ROS
//        if (getBluetoothService().getIsBTConnected()) {
//            sendMessage("m;" +
//                    (int) (-y * 200) +
//                    ";" +
//                    (int) (x * 200)
//            );
//        }
    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis) :
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private LinearLayout vrImageActivityLayout;

    private YawPitchPublisher yawPitchPublisher;

    private VrPanoramaView imageView;
    private ImageView imageLeft;
    private ImageView imageRight;

    private JoystickPublisher joystickPublisher;

}


class JoystickPublisher implements NodeMain {



    Publisher<geometry_msgs.Vector3> publisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("joystick_publisher");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        publisher = connectedNode.newPublisher("phone/joystick", "geometry_msgs/Vector3");
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


    public void publishMessage(float x, float y) {

        geometry_msgs.Vector3 msg = publisher.newMessage();

        msg.setX(x);
        msg.setY(y);
        publisher.publish(msg);
    }
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