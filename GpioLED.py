import RPi.GPIO as GPIO
import time

BUZZ_AC = 4
BUZZ_PAS = 17

GPIO.setmode(GPIO.BCM)
GPIO.setup(BUZZ_AC, GPIO.OUT)
GPIO.setup(BUZZ_PAS, GPIO.OUT)

while 1:
        print "AC-ON, PAS-OFF"
        GPIO.output(BUZZ_AC, True)
        GPIO.output(BUZZ_PAS, False)
        time.sleep(5)
        print "AC-OFF, PAS-ON"
        GPIO.output(BUZZ_AC, False)
        GPIO.output(BUZZ_PAS, True)
        time.sleep(5)
