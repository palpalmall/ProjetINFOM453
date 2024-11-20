import json
from Utils_RPi_Arduino import activate_ping_figurine, activate_mood_figurine, activate_status_figurine

def sendPing( data_struct, websocket) :
    json_data_struct = json.dumps(data_struct)
    websocket.send(json_data_struct)
    #message = websocket.recv()
    #print(f"Received: {message}")

def receivePing(data_struct, id_figurine) :
    activate_ping_figurine(id_figurine)

def receiveStatus(data_struct, id_figurine) :
    status = data_struct["status"]
    activate_status_figurine(id_figurine, status)

def receiveMood(data_struct, id_figurine):
    mood = data_struct["mood"]
    activate_mood_figurine(id_figurine, mood)