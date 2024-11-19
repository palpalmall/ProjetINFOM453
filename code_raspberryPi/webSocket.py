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
    "type" : "ping",
    "id" : 1015,
    "nom" : "Marlene",
    "id_sender" : 1010
}

def activate_ping_figurine(id_figurine):
    print("ping figurine %d" %id_figurine)
def activate_status_figurine(id_figurine, status):
    print("status is %s" %status)
def activate_mood_figurine(id_figurine, mood):
    print("mood is %s" %mood)

def sendPing( data_struct, websocket = my_websocket) :
    json_data_struct = json.dumps(data_struct)
    websocket.send(json_data_struct)
    message = websocket.recv()
    print(f"Received: {message}")

def receivePing(data_struct, id_figurine) :
    activate_ping_figurine(id_figurine)

def receiveStatus(data_struct, id_figurine) :
    status = data_struct["status"]
    activate_status_figurine(id_figurine, status)

def receiveMood(data_struct, id_figurine):
    mood = data_struct["mood"]
    activate_mood_figurine(id_figurine, mood)

sendPing(ping)

while True :
    json_data_struct = my_websocket.recv()
    data_struct = json.loads(json_data_struct)

    id_figurine = data_struct["id_sender"]
    type = data_struct["type"]

    match type :
        
        case "ping" :
            receivePing(data_struct, id_figurine)
        
        case "status" :
            receiveStatus(data_struct, id_figurine)

        case "mood" :
            receiveMood(data_struct, id_figurine)