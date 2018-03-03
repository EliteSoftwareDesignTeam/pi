# file: rfcomm-server.py
# auth: Albert Huang <albert@csail.mit.edu>
# desc: simple demonstration of a server application that uses RFCOMM sockets
#
# $Id: rfcomm-server.py 518 2007-08-10 07:20:07Z albert $

from bluetooth import *
import threading
import sys
from __future__ import print_function

def put_stdout(msg):
    print msg
    sys.stdout.flush()

def put_stderr(msg):
    print(msg, file=sys.stderr)
    sys.stderr.flush()

def put_bt(msg):
    put_stdout("Sending %s" % msg)
    client_sock.send(msg)
    client_sock.flush()

def get_stdin():
    return sys.stdin.readline()

def get_bt():
    return client_sock.recv(1024)

def stdin_loop():
    while True:
        input = get_stdin()
        put_stderr(input)
        put_bt(input)

server_sock = BluetoothSocket(RFCOMM)
server_sock.bind(("", PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service(server_sock, "SampleServer",
                  service_id=uuid,
                  service_classes=[uuid, SERIAL_PORT_CLASS],
                  profiles=[SERIAL_PORT_PROFILE]
                  )

put_stderr("Waiting for connection on RFCOMM channel %d" % 1)

client_sock, client_info = server_sock.accept()
put_stderr("Accepted connection")

t1 = threading.Thread(target=stdin_loop, args=())
t1.start()

try:
    while True:
        data = get_bt()
        if len(data) == 0: break
        put_stdout(data)
except IOError:
    pass

put_stderr("Disconnected")

client_sock.close()
server_sock.close()