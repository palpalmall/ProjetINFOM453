import json
import asyncio
from websockets.sync.client import connect
from Utils_websocket import receiveMood, receivePing, receiveStatus, sendPing

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
async def check_message():
    while True :
        try:
            json_data_struct = my_websocket.recv()
            print("cm2")
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
        except :
            pass
        
        #await asyncio.sleep(2)

async def make_smth_else():
    while True :
        ##check if a head has been smashed
        print("making something else...")
        ping = {
            "type" : "ping",
            "id" : 1015,
            "nom" : "Marlene",
            "id_sender" : 1010
        }

        sendPing(ping, my_websocket)
        print('nothing for now')
        await asyncio.sleep(2)

async def main():
    
    await asyncio.gather(check_message(), make_smth_else())


asyncio.run(main())
