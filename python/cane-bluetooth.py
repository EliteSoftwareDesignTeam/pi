# file: rfcomm-server.py
# auth: Albert Huang <albert@csail.mit.edu>
# desc: simple demonstration of a server application that uses RFCOMM sockets
#
# $Id: rfcomm-server.py 518 2007-08-10 07:20:07Z albert $

from bluetooth import *
import threading
import sys

def put_stdout(msg):
    print msg
    sys.stdout.flush()

def put_bt(msg):
    client_sock.send(msg)

def get_stdin():
    return sys.stdin.read()

def get_bt():
    return client_sock.recv(1024)

def stdin_loop():
    while True:
        input = get_stdin()
        put_bt(input)

server_sock = BluetoothSocket(RFCOMM)
server_sock.bind(("", PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service(server_sock, "SampleServer",
                  service_id=uuid,
                  service_classes=[uuid, SERIAL_PORT_CLASS],
                  profiles=[SERIAL_PORT_PROFILE],
                  #                   protocols = [ OBEX_UUID ]
                  )

#put_stdout("Waiting for connection on RFCOMM channel %d" % port)

client_sock, client_info = server_sock.accept()
#put_stdout("Accepted connection")

t1 = threading.Thread(target=stdin_loop, args=())
t1.start()

try:
    while True:
        data = get_bt()
        if len(data) == 0: break
        put_stdout(data)
except IOError:
    pass

# put_stdout("disconnected")

client_sock.close()
server_sock.close()
# put_stdout("all done")
