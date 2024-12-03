import json, time, threading
from Utils_web_requests import *
from Utils_RPi_Arduino import askForSmashedHead, onTag, RFID_init, Repeat, get_temperature, temperature_init

NamesAddrDico = {
	
}

config_done = False
login, psw = TODO

# d'abord, il faut recup le login password avec bluetooth
my_id, team_nbr = get_init(login, psw)

ch = RFID_init(onTag, NamesAddrDico)
tempSensor = temperature_init()

# launch a thread which send temperature to the server every hour
t = Repeat(3600, lambda: post_temperature(team_nbr, my_id, get_temperature(tempSensor)))
t.start()

#check figurines connected to he RPi
#send figurines availables to the server

if(my_id != -1 and team_nbr != -1):
    config_done = True

while config_done :

    # faire les get
    get_ping(team_nbr, my_id) # get ping from server AND send it to the figurine
    get_status(team_nbr, my_id) # get status from server AND end it to the figurine
    get_mood(team_nbr, my_id) # get mood from server AND end it to the figurine
    
    ##check if a head has been smashed
    smashed_head_dico = askForSmashedHead(NamesAddrDico)
    for id_fig in smashed_head_dico.keys():
        if(smashed_head_dico[id_fig]):
            post_ping(team_nbr, my_id, id_fig)
