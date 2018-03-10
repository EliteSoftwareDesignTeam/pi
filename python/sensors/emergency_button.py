import RPi.GPIO as GPIO
import time

BTN_PIN = 18

GPIO.setmode(GPIO.BCM)
#GPIO.setup(BTN_PIN, GPIO.IN)
GPIO.setup(BTN_PIN, GPIO.IN, pull_up_down=GPIO.PUD_UP)

input_last_state = True;

while True:
    input_state = GPIO.input(BTN_PIN)
    if input_state == False:
        if input_state != input_last_state:
            print "PRESSED"
            time.sleep(0.2)
    input_last_state = input_state