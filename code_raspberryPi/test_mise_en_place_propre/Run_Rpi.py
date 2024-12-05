import json, time, threading, asyncio
from Utils_web_requests import *
from Utils_RPi_Arduino import askForSmashedHead, onTag, RFID_init, Repeat, get_temperature, temperature_init
from bleServer import run

NamesAddrDico = {
	
}

config_done = False
#login, psw = TODO

# d'abord, il faut recup le login password avec bluetooth
#my_id, team_nbr = get_init(login, psw)

#on démarre le serveur ble pour récupérer les infos id team et figurine id (en plus d'essayer de se co au wifi)
loop = asyncio.get_event_loop()
ids = loop.run_until_complete(run(loop))
print("ids structure : ")
print(ids)
team_nbr = ids["id_team"]
my_id = ids["id_figurine"]

ch = RFID_init(onTag, NamesAddrDico)
tempSensor = temperature_init()

# launch a thread which send temperature to the server every hour
t = Repeat(3600, lambda: post_temperature(team_nbr, my_id, get_temperature(tempSensor)))
t.start()

#check figurines connected to he RPi
#send figurines availables to the server
while len(NamesAddrDico) <= 0 and my_id == -1 and team_nbr == -1:
    pass

while True :

    # faire les get
    get_ping(team_nbr, my_id) # get ping from server AND send it to the figurine
    get_status(team_nbr, my_id) # get status from server AND end it to the figurine
    get_mood(team_nbr, my_id) # get mood from server AND end it to the figurine
    
    ##check if a head has been smashed
    smashed_head_dico = askForSmashedHead(NamesAddrDico)
    for id_fig in smashed_head_dico.keys():
        if(smashed_head_dico[id_fig]):
            post_ping(team_nbr, my_id, id_fig)
