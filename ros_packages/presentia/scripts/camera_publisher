#!/usr/bin/python
#
# The MIT License (MIT)
# 
# Copyright (c) 2015 Australian Centre for Robotic Vision
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

"""
camera_publisher: Camera publisher node

------------
Mode of use
------------
$ rosrun pybot camera_publisher _topic:=<image_topic> _camera:=<int> _resolution:=<[width, height]>

_topic: compressed image topic path to publish, default 'pybot/camera/image_color/compressed'
_camera: camera index, default 0
_resollution: output image resolution, default [320, 240]

"""

__author__      = 'Juan David Adarve'
__email__       = 'juan.adarve@anu.edu.au'
__license__     = 'MIT'
__copyright__   = 'Copyright 2015, Australian Centre for Robotic Vision'

import threading
import rospy
import cv2
import numpy as np

from geometry_msgs.msg import Twist
# from cv_bridge import CvBridge, CvBridgeError
from sensor_msgs.msg import Image, CompressedImage

class CameraPublisher(object):
    
    def __init__(self, topic='camera/image_color/compressed', camindex=0, resolution=(640,480)):
        
        # creates a ROS publisher for compressed images
        self.__publisher = rospy.Publisher(topic, CompressedImage, queue_size=1)
        
        self.__capture = cv2.VideoCapture(camindex)
        if self.__capture == None:
            rospy.logerr('CameraPublisher: capture device not found')
            quit()
        else:
            rospy.loginfo('CameraPublisher: capture device found')
        
        self.__capture.set(cv2.CAP_PROP_FRAME_WIDTH, resolution[0])
        self.__capture.set(cv2.CAP_PROP_FRAME_HEIGHT, resolution[1])
    
        rospy.loginfo('CameraPublisher: starting capture loop')
        self.__imgThread = threading.Thread(target=self.__imageLoop)
        self.__imgThread.start()
    
    
    def __imageLoop(self):
        """
        Image acquisition and processing loop.
        
        This method constantly reads an image from the capture device and
        compresses it and publishes it in the ROS topic
        """
        
        # 10 Hz frame rate
        self.__rate = rospy.Rate(30)
        
        while not rospy.is_shutdown():
            
            try:
                # reads a new image from the camera
                self.__imgBGR = self.__capture.read()[1]
                
                if self.__imgBGR != None:

                    # vertical flip
                    self.__imgBGR = cv2.flip(self.__imgBGR, flipCode=-1)
                    
                    # creates a compressed image message
                    imgMsg = CompressedImage()
                    imgMsg.header.stamp = rospy.Time.now()
                    imgMsg.format = 'jpeg'
                    imgMsg.data = np.array(cv2.imencode('.jpeg', self.__imgBGR)[1]).tostring()
                     
                    # publish the image
                    self.__publisher.publish(imgMsg)
                    
                    # cv2.imshow('camera', self.__imgBGR)
                    # cv2.waitKey(10)
                    
                else:
                    rospy.logerr('CameraPublisher: error: no image read from camera')
                    
                self.__rate.sleep()
                
            except Exception as e:
                rospy.logerr('CameraPublisher: error reading image frame: {0}'.format(e))


    def close(self):

        self.__capture.release()

###########################################################
# ENTRY POINT
###########################################################
if __name__ == '__main__':
    
    # init ros node
    rospy.init_node('camera_publisher')
    rospy.loginfo('camera_publisher: start')
    
    # read node parameters
    topic = rospy.get_param('camera_publisher/topic', 'camera/image_color/compressed')
    camindex = rospy.get_param('camera_publisher/camera', 0)
    resolution = rospy.get_param('camera_publisher/resolution', [240, 240])
    
    rospy.loginfo('topic name: {0}'.format(topic))
    rospy.loginfo('camera index: {0}'.format(camindex))
    rospy.loginfo('image resolution: {0}'.format(resolution))
    
    webCamPub = CameraPublisher(topic, camindex, resolution)
    
    try:
        rospy.spin()
    except KeyboardInterrupt:
        rospy.loginfo('camera_publisher: keyboard interrupt, shutting down')
    
    webCamPub.close()
    cv2.destroyAllWindows()
