import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BCM)

TRIG = 17
ECHO = 4

print "Distance measurement in progress"

GPIO.setup(TRIG,GPIO.OUT)
GPIO.setup(ECHO,GPIO.IN)

while True:

    GPIO.output(TRIG, False)
    print "Waitng For Sensor To Settle"
    time.sleep(2)

    GPIO.output(TRIG, True)
    time.sleep(0.00001)
    GPIO.output(TRIG, False)

    while GPIO.input(ECHO)==0:
        pass

    pulse_start = time.time()

    while GPIO.input(ECHO)==1:
        pass

    pulse_duration = time.time() - pulse_start

    distance = pulse_duration * 17150
    distance = round(distance, 2)

    if 2 < distance < 400:
        print "Distance:",distance - 0.5,"cm"
    else:
        print "Out Of Range"