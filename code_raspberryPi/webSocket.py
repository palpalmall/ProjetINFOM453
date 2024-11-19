#!/usr/bin/env python

from websockets.sync.client import connect
import json

def hello():
    with connect("ws://localhost:8765") as websocket:
        websocket.send("Hello world!")
        message = websocket.recv()
        print(f"Received: {message}")

#hello()

my_websocket = connect("ws://localhost:8765")
ping = {
    "id" : 1015,
    "nom" : "Marlene",
    "id_sender" : 1010
}

def sendPing(websocket, data_struct) :
    json_data_struct = json.dumps(data_struct)
    websocket.send(json_data_struct)
    message = websocket.recv()
    print(f"Received: {message}")

sendPing(my_websocket, ping)