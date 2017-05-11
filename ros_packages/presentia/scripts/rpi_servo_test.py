import RPi.GPIO as GPIO
import time


if __name__ == '__main__':
    

    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(16, GPIO.OUT)

    p = GPIO.PWM(16, 50)
    p.start(7.5)

    try:

        while True:
            p.ChangeDutyCycle(0.0)
            time.sleep(0.5)
            p.ChangeDutyCycle(5.0);
            time.sleep(0.5)
            p.ChangeDutyCycle(10.0);
            time.sleep(0.5)
            # p.ChangeDutyCycle(7.5)  # turn towards 90 degree
            # time.sleep(1) # sleep 1 second
            # p.ChangeDutyCycle(2.5)  # turn towards 0 degree
            # time.sleep(1) # sleep 1 second
            # p.ChangeDutyCycle(12.5) # turn towards 180 degree
            # time.sleep(1) # sleep 1 second 

            print('end while')

    except KeyboardInterrupt:
        p.stop()
        GPIO.cleanup()

# import pigpio


# SERVO_PAN_PIN = 12


# def main():

#     print('STARTING')

#     servoPan = pigpio.pi()
#     servoPan.set_mode(SERVO_PAN_PIN, pigpio.OUTPUT)
#     servoPan.hardware_PWM(SERVO_PAN_PIN, 50, 1)


#     print('FINISHED')


# if __name__ == '__main__':
#     main()