#!/usr/bin/python

import array
import serial
import numpy as np

import rospy
from geometry_msgs.msg import QuaternionStamped
from geometry_msgs.msg import Vector3

import quaternion


serialPort = None
servoFrame = 'M;{0:d};{1:d}\n'



def joystickCallback(msg):

    motorLeft, motorRight = int(msg.x), int(msg.y)
    sendServoCommand(motorLeft, motorRight)


def sendServoCommand(motorLeft, motorRight):
    
    frameStr = servoFrame.format(motorLeft, motorRight)
    # print('{0} : {1} : {2}'.format(motorLeft, motorRight, frameStr))

    # rotate de vehicle or pantilt according to the yaw value
    serialPort.write(array.array('b', frameStr))
    serialPort.flush()


if __name__ == '__main__':

    global serialPort
    
    rospy.init_node('vehicle_control')
    rospy.loginfo('vehicle_control node started')

    serialPort = serial.Serial('/dev/ttyACM1', 115200, )  # open serial port
    rospy.loginfo('serial port: ' + serialPort.name)         # check which port was really used
    
    #ser.close()             # close port

    joystickSubscriber = rospy.Subscriber('phone/joystick',
        Vector3, joystickCallback, queue_size=1)    


    rospy.spin()
    serialPort.close()

    rospy.loginfo('vehicle_control node finished')

