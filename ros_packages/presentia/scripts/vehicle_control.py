#!/usr/bin/python

import array
import serial
import numpy as np

import rospy
from geometry_msgs.msg import QuaternionStamped
from geometry_msgs.msg import Vector3

import quaternion


serialPort = None
servoFrame = 'S;{0:d};{1:d};{2:d};{3:d}\n'

degYaw      = 0
degPitch    = 0
motorLeft   = 0
motorRight  = 0

def orientationCallback(msg):

    global servoFrame, degYaw, degPitch

    _, degYaw, degPitch = int(msg.x), int(msg.y) + 90, int(msg.z) + 35

    # frameStr = servoFrame.format(degYaw, degPitch)
    # print('{0} : {1} : {2}'.format(degYaw, degPitch, frameStr))

    # rotate de vehicle or pantilt according to the yaw value
    # serialPort.write(array.array('b', frameStr))
    # serialPort.flush()

    sendCommand()


def joystickCallback(msg):

    global motorLeft, motorRight
    motorLeft, motorRight = int(msg.x), int(msg.y)

    # sendCommand()


def sendCommand():

    global degYaw, degPitch, motorLeft, motorRight

    frameStr = servoFrame.format(degYaw, degPitch, motorLeft, motorRight)

    print(frameStr)

    # serialPort.write(array.array('b', frameStr))
    # serialPort.plush()


if __name__ == '__main__':

    global serialPort
    
    rospy.init_node('servo_control')
    rospy.loginfo('servo_control node started')

    # serialPort = serial.Serial('/dev/ttyUSB0', 115200, )  # open serial port
    # rospy.loginfo('serial port: ' + serialPort.name)         # check which port was really used
    
    #ser.close()             # close port

    orientationSubscriber = rospy.Subscriber('phone/head_orientation',
        Vector3, orientationCallback, queue_size=1)

    joystickSubscriber = rospy.Subscriber('phone/joystick',
        Vector3, joystickCallback, queue_size=1)    


    rospy.spin()

    # serialPort.close()

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
