# Enable SSH on Ubuntu MATE

```
sudo apt-get update
sudo apt-get install openssh-server
sudo ufw allow 22
sudo /etc/init.d/ssh start
```

Enable ssh server on boot

```
sudo systemctl enable ssh.service
```



# GStreamer commands

gst-launch-1.0 v4l2src ! videoconvert ! videoscale ! video/x-raw,width=800,height=600 ! vp8enc ! rtpvp8pay ! udpsink host=192.168.1.11 port=5100


gst-launch-1.0 v4l2src ! videoconvert ! videoscale ! video/x-raw,width=800,height=600 ! avenc_mpeg4 ! rtpmp4vpay config-interval=3 ! udpsink host=127.0.0.1 port=5200

gst-launch-1.0 v4l2src ! videoconvert ! videoscale ! video/x-raw,format=I420,width=800,height=600,framerate=25/1 ! jpegenc ! rtpjpegpay ! udpsink host=127.0.0.1 port=5000



gst-launch-1.0 v4l2src ! image/jpeg,width=1280,height=720 ! tcpserversink host=127.0.0.1:4000



# HLS

gst-launch-1.0 v4l2src ! x264enc ! mpegtsmux ! hlssink target-duration=0


gst-launch-1.0 v4l2src ! x264enc ! mpegtsmux ! filesink location=file.mp4