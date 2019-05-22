import socket
import os
import subprocess

s = socket.socket()
host = '192.168.0.101'
port = 6000

print("Connecting to server")
s.connect((host, port))
print("Connected")

first_signal = True
while True:
    data = s.recv(1024).decode("utf-8")
    if data == ' ':
        s.send(str.encode("\n"))
        continue
    if len(data) > 0:
        print(data)
        reflex_data = input()
        if "exit" in reflex_data:
            print("close connection")
            s.send(str.encode("terminating"))
            s.close()
            exit()
        s.send(str.encode(reflex_data + '\n'))
        # s.send(reflex_data)