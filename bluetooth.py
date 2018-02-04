from bluetooth import *

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "CanePi",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ],
#                   protocols = [ OBEX_UUID ]
                    )

while True:
    print "Waiting for connection on RFCOMM channel %d" % port
    client_sock, client_info = server_sock.accept()
    print "Received connection from ", client_info

    try:
        data = client_sock.recv(1024)
        if len(data) == 0: break
        print "Received [%s]" % data

        # Process
        print "Sending [%s]" % data
        #client_sock.send(data)

    except IOError:
        pass

    except KeyboardInterrupt:
        print "Disconnected"
        client_sock.close()
        server_sock.close()
        break