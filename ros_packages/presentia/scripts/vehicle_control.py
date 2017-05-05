#!/usr/bin/python


import rospy

from geometry_msgs.msg import QuaternionStamped

import quaternion


def orientationCallback(msg):

    print('quaternion received')


if __name__ == '__main__':
    
    rospy.init_node('vehicle_control')
    rospy.loginfo('vehicle_control node started')

    orientationSubscriber = rospy.Subscriber('phone/orientation',
        QuaternionStamped, orientationCallback, queue_size=1)


    rospy.spin()

    rospy.loginfo('vehicle_control node finished')
