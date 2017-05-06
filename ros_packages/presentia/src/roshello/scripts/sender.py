#!/usr/bin/python

from threading import Thread

import rospy
from std_msgs.msg import String


def senderThread():

    publisher = rospy.Publisher('/roshello/greeting',
                                String,
                                queue_size=1)
    
    counter = 0
    loopRate = rospy.Rate(1)        # 1 Hz
    while not rospy.is_shutdown():
        
        msg = String()
        msg.data = 'hello from sender: {0}'.format(counter)
        counter += 1
        
        publisher.publish(msg)
        loopRate.sleep()
        

#######################################
# ENTRY POINT
#######################################
if __name__ == '__main__':

    # initialise ROS node
    rospy.init_node('hello_world_sender')
    rospy.loginfo('Sender node started')
    
    thr = Thread(target=senderThread)
    thr.start()
    
    rospy.spin()
    
    thr.join()
    rospy.loginfo('Sender node finished')
    
