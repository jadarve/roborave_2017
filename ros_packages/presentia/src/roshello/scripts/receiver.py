#!/usr/bin/python

import rospy
from std_msgs.msg import String


def callbackFunction(msg):    
    print(msg.data)
    

#######################################
# ENTRY POINT
#######################################
if __name__ == '__main__':

    # initialise ROS node
    rospy.init_node('hello_world_receiver')
    rospy.loginfo('Receiver node started')
    
    subscriber = rospy.Subscriber('roshello/greeting',
        String,
        callbackFunction,
        queue_size=1)
    
    rospy.spin()
    
    rospy.loginfo('Receiver node finished')
    
