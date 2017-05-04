import cv2
import sys
import time
import tty
import termios
import threading
import urllib2

def getChar():
    fd = sys.stdin.fileno()
    oldSettins = termios.tcgetattr(fd)

    try:
        tty.setraw(fd)
        ch = sys.stdin.read(1)
    finally:
        termios.tcsetattr(fd, termios.TCSADRAIN, oldSettins)

    return ch


def credentials(url, username, password):

    p = urllib2.HTTPPasswordMgrWithDefaultRealm()
    p.add_password(None, url, username, password)
    handler = urllib2.HTTPBasicAuthHandler(p)
    opener = urllib2.build_opener(handler)
    urllib2.install_opener(opener)
    

def keyboardThread(run):


    bindings = { 'a': 'left',
                 'd': 'right',
                 'w': 'up',
                 's': 'down'}

    cmd = 'http://192.168.1.14/cgi-bin/hi3510/ptzctrl.cgi?-step=0&-act={0}&-speed=45'
    credentials(cmd, 'admin', 'admin')

    while not run.wait(0):

        try:
            char = getChar()

            print('char: {0}'.format(char))
            if char == 'e':
                print('exit')
                exit()


            try:
                # response = urllib2.urlopen(cmd.format(bindings[char])).read()
                # print(response)
                urllib2.urlopen(cmd.format(bindings[char]))

                # time.sleep(0.5)
                # content = urllib2.urlopen(cmd.format('stop')).read()


            except KeyError:
                pass
                # print('not found')



        except KeyboardInterrupt:
            print('Keyboard interrpt in keyb thread')
            run.set()



def cameraThread(run, address, user, password):

    url = 'rtsp://{0}:{1}@{2}/iphone/12'.format(user, password, address)
    cam = cv2.VideoCapture(url)
    print(cam.isOpened())

    cv2.namedWindow('camera')

    while not run.wait(0):

        _, img = cam.read()
        cv2.imshow('camera', img)
        cv2.waitKey(10)



if __name__ == '__main__':

    print('start')

    address     = '192.168.1.14'
    user        = 'admin'
    password    = 'admin'

    run = threading.Event()
    camThread = threading.Thread(target=cameraThread, args=(run, address, user, password))
    keybThread = threading.Thread(target=keyboardThread, args=(run, ))

    camThread.start()
    keybThread.start()

    try:

        while True:
            time.sleep(1)

    except KeyboardInterrupt:

        print('Keyboard interrupt received')

        run.set()
        camThread.join()
        keybThread.join()
        exit(-1)
