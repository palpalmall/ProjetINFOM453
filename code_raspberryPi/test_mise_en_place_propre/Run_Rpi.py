import json
from Utils_web_requests import *
from Utils_RPi_Arduino import askForSmashedHead

NamesAddrDico = {
	"Josephine" : 0x8,# mkr1000 address
	"Richard" : 0x3C, #uno R3 address
}

login, psw = TODO
# d'abord, il faut recup le login password avec bluetooth
my_id, team_nbr = get_init()

while True :
    # faire les get
    get_ping(team_nbr, my_id)
    get_status(team_nbr, my_id)
    get_mood(team_nbr, my_id)
    
    ##check if a head has been smashed
    smashed_head_dico = askForSmashedHead(NamesAddrDico)
    for id_fig in smashed_head_dico.keys():
        if(smashed_head_dico[id_fig]):
            post_ping(team_nbr, my_id, id_fig)
