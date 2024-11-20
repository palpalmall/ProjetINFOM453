import json
from websockets.sync.client import connect
from Utils_websocket import receiveMood, receivePing, receiveStatus

my_websocket = connect("ws://localhost:8765")

"""
Je pars du principe que les messages qui seront envoyé/reçu par websocket seront de la sorte :
{
  type : type du message (ping, status, mood),
  id_sender : id de la personne qui envoye,
  id_receiver : id de la personne qui recoit
  mood* : mood de la personne (optionnal si type == mood),
  status* : status de la personne (optionnal si type == status) 
}

"""

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