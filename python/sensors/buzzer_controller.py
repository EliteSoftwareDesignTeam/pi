import RPi.GPIO as GPIO
import time
import sys

GPIO.setmode(GPIO.BCM)
BUZZ_AC = 4
BUZZ_PAS = 17
GPIO.setup(BUZZ_AC, GPIO.OUT)
GPIO.setup(BUZZ_PAS, GPIO.OUT)

GPIO.output(BUZZ_AC, False)
GPIO.output(BUZZ_PAS, False)

line = sys.stdin.readline()
while line:
        print "Read [%s]" % line
        try:
                direction,delay = line.split(",")
                print direction
                print delay
        except:
                print "Couldn't unpack direction and time"

        try:
                delay_val = int(delay)
                if direction == "left":
                        GPIO.output(BUZZ_AC, True)
                        print "sleeping"
                        time.sleep(delay_val)
                        print "done sleeping"
                        GPIO.output(BUZZ_AC, False)
                elif direction == "right":
                        GPIO.output(BUZZ_PAS, True)
                        time.sleep(delay_val)
                        GPIO.output(BUZZ_PAS, False)
        except ValueError:
                print("Time must be an int")
        line = sys.stdin.readline()
