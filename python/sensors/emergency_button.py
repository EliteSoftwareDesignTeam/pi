import RPi.GPIO as GPIO
import time

BTN_PIN = 4
GPIO.setmode(GPIO.BCM)
GPIO.setup(BTN_PIN, GPIO.IN)
# GPIO.setup(18, GPIO.IN, pull_up_down=GPIO.PUD_UP)

while True:
    if GPIO.input(BTN_PIN):
        print "Button pressed"
        time.sleep(0.2)