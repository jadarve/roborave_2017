#!/usr/bin/python

import array
import serial
import numpy as np

import rospy
from geometry_msgs.msg import QuaternionStamped
from geometry_msgs.msg import Vector3

import quaternion


serialPort = None
servoFrame = 'S{0:d};{1:d};\n'


def orientationCallback(msg):

    global servoFrame

    degYaw, degPitch = max(0, min(int(msg.y) + 90, 180)), int(msg.z) + 35
    sendServoCommand(degYaw, degPitch)


def sendServoCommand(degYaw, degPitch):
    
    frameStr = servoFrame.format(degYaw, degPitch)
    print('{0} : {1} : {2}'.format(degYaw, degPitch, frameStr))

    # rotate de vehicle or pantilt according to the yaw value
    serialPort.write(array.array('b', frameStr))
    serialPort.flush()    


if __name__ == '__main__':

    global serialPort
    
    rospy.init_node('servo_control')
    rospy.loginfo('servo_control node started')

    serialPort = serial.Serial('/dev/ttyACM0', 115200)       # open serial port
    rospy.loginfo('serial port: ' + serialPort.name)         # check which port was really used
    
    # recenter the servos
    sendServoCommand(90, 35)

    # orientationSubscriber = rospy.Subscriber('phone/head_orientation',
    #     Vector3, orientationCallback, queue_size=1)


    rospy.spin()
    serialPort.close()

    rospy.loginfo('servo_control node finished')


# def orientationCallback(msg):

#     global servoFrame

#     header = msg.header

#     q = msg.quaternion
#     q = np.array([q.x, q.y, q.z, q.w], dtype=np.float32)

#     # get the yaw pitch and roll from the quaternion
#     yaw, pitch, roll = quaternion.q2Euler(q)

#     degYaw = int(((180 * yaw / np.pi) + 180) / 2)
#     degPitch = int(((180 * pitch / np.pi) + 180) / 2)

#     frameStr = servoFrame.format(degYaw, degPitch)
#     print('{0} : {1} : {2}'.format(degYaw, degPitch, frameStr))

#     serialPort.write(array.array('b', frameStr))
#     serialPort.flush()
#     # rotate de vehicle or pantilt according to the yaw value



# if __name__ == '__main__':

#     global serialPort
    
#     rospy.init_node('servo_control')
#     rospy.loginfo('servo_control node started')

#     serialPort = serial.Serial('/dev/ttyUSB0', 115200, )  # open serial port
#     rospy.loginfo('serial port: ' + serialPort.name)         # check which port was really used
    
#     #ser.close()             # close port

#     orientationSubscriber = rospy.Subscriber('phone/orientation',
#         QuaternionStamped, orientationCallback, queue_size=1)


#     rospy.spin()

#     serialPort.close()

#     rospy.loginfo('servo_control node finished')
