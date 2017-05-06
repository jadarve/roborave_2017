#!/usr/bin/python

import numpy as np

import rospy

from geometry_msgs.msg import QuaternionStamped, Vector3

import quaternion




def orientationCallback(msg):

    header = msg.header

    q = msg.quaternion
    q = np.array([q.x, q.y, q.z, q.w], dtype=np.float32)

    # get the yaw pitch and roll from the quaternion
    v = Vector3()
    v.z, v.y, v.x = quaternion.q2Euler(q)
    anglePublisher.publish(v)

    #print(yaw)
    #print(quaternion.q2Euler(q))

    # rotate de vehicle or pantilt according to the yaw value



if __name__ == '__main__':
    
    rospy.init_node('vehicle_control')
    rospy.loginfo('vehicle_control node started')

    orientationSubscriber = rospy.Subscriber('phone/orientation',
        QuaternionStamped, orientationCallback, queue_size=1)

    anglePublisher = rospy.Publisher('phone/orientation_rpy', Vector3, queue_size=1)

    rospy.spin()

    rospy.loginfo('vehicle_control node finished')
