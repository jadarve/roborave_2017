package construyelab.org.rospubsub;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.video.VrVideoView;

import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ///////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////

//    public VRActivity() {
//        super("VRActivity", "VRActivity");
//
////        float[] headRotation = new float[2];
////        videoView.getHeadRotation(headRotation);
//    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vr);

        videoView = (VrVideoView)findViewById(R.id.VRVideoView);
//        try {
//            VrVideoView.Options opt = new VrVideoView.Options();
//            opt.inputFormat = VrVideoView.Options.FORMAT_HLS;
//            opt.inputType = VrVideoView.Options.TYPE_MONO;
//            videoView.loadVideo(Uri.parse("http://127.0.0.1:8080/playlist.m3u8"), opt);
////            videoView.playVideo();
//        } catch(IOException e) {
//
//        }

        vrView = videoView;
//        vrView = (VrPanoramaView)findViewById(R.id.VRVideoView);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("construyelab", "VRActivity.onResume(): playing video");
        try {
            VrVideoView.Options opt = new VrVideoView.Options();
//            opt.inputFormat = VrVideoView.Options.FORMAT_HLS;
//            opt.inputType = VrVideoView.Options.TYPE_MONO;
//            videoView.loadVideo(Uri.parse("http://192.168.1.11:8080/playlist.m3u8"), opt);
//            videoView.seekTo(videoView.getDuration() -1);

            opt.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
            opt.inputType = VrVideoView.Options.TYPE_MONO;
            videoView.loadVideo(Uri.parse("http://192.168.1.32:8080/phd-movie-720p.mp4"), opt);

//            opt.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
//            opt.inputType = VrVideoView.Options.TYPE_MONO;
//            videoView.loadVideo(Uri.parse("rtp://192.168.1.11:5100"), opt);
        } catch(IOException e) {

        }
        Log.d("construyelab", "VRActivity.onResume(): playing video moni");

        vrView.resumeRendering();
    }

    com.google.vr.sdk.widgets.video.VrVideoView videoView;

    private VrWidgetView vrView;
}
