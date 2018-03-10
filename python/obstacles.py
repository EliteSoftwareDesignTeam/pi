import RPi.GPIO as GPIO
import time
import sys

def delay_microseconds(micros):
    time.sleep(0.000001 * micros)

def delay_milliseconds(millis):
    delay_microseconds(1000 * millis)

def microseconds_to_cm(micros):
    return micros * 100 / 5882

GPIO.setmode(GPIO.BCM)
ECHO_PIN = 4
TRIGGER_PIN = 17
GPIO.setup(ECHO_PIN, GPIO.IN)
GPIO.setup(TRIGGER_PIN, GPIO.OUT)

def pulse_in(pin, val):
    while GPIO.input(pin) != val:
        continue
    start = time.time()
    while GPIO.input(pin) == val:
        continue
    return (time.time() - start) / 1000000

line = sys.stdin.readline()
while line:
    #if line == "get_distance":
    print "getting distance"
    GPIO.output(TRIGGER_PIN, GPIO.LOW)
    delay_microseconds(2)
    print "triggering"
    GPIO.output(TRIGGER_PIN, GPIO.HIGH)
    delay_microseconds(10)
    GPIO.output(TRIGGER_PIN, GPIO.LOW)
    delay_microseconds(2)
    print "triggered"
    duration = pulse_in(ECHO_PIN, GPIO.HIGH)
    centimetres = microseconds_to_cm(duration)
    print centimetres
    delay_milliseconds(1000)
    #line = sys.stdin.readline()