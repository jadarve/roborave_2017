#!/usr/bin/python

import numpy as np

import rospy

from geometry_msgs.msg import QuaternionStamped

import quaternion




def orientationCallback(msg):

    header = msg.header

    q = msg.quaternion
    q = np.array([q.x, q.y, q.z, q.w], dtype=np.float32)

    # get the yaw pitch and roll from the quaternion
    yaw, pitch, roll = quaternion.q2Euler(q)

    print(yaw)

    # rotate de vehicle or pantilt according to the yaw value



if __name__ == '__main__':
    
    rospy.init_node('vehicle_control')
    rospy.loginfo('vehicle_control node started')

    orientationSubscriber = rospy.Subscriber('phone/orientation',
        QuaternionStamped, orientationCallback, queue_size=1)


    rospy.spin()

    rospy.loginfo('vehicle_control node finished')
