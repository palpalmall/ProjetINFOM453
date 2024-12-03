
# from smbus import SMBus
# import time

# bus = SMBus(1) # indicates /dev/ic2-1

# #Nom des figurines et leurs adresses arduino
# NamesAddrDico = {
# 	"Josephine" : 0x8,# mkr1000 address
# 	"Richard" : 0x3C, #uno R3 address
# }

# def activate_ping_figurine(id_figurine):
#   print("ping figurine %s" %id_figurine)
#   addr = NamesAddrDico[id_figurine]
#   bus.write_i2c_block_data(addr,4, [1]) # switch servo motor on

# def askForSmashedHead(NamesAddrDico):
# 	smashedHeadDico = {}
# 	for namesAddr in NamesAddrDico.keys():
# 		try:
# 			smashed = bus.read_i2c_block_data(NamesAddrDico[namesAddr], 2, 1)
# 			smashedHeadDico[namesAddr] = smashed
# 		except :
# 			print("name %s does not exist in the addresses dico" %(namesAddr))
				
# 	#send the dico to the server 
# 	print(smashedHeadDico)
# 	return smashedHeadDico

# while  True :
#     response = askForSmashedHead(NamesAddrDico)
    
#     for id_figurine in response.keys():
#         if(response[id_figurine][0] == 1):
#             activate_ping_figurine('Josephine')
#     time.sleep(1)

# ================================ TEST RFID ==================================
import json, time, threading
from Utils_web_requests import *
from Utils_RPi_Arduino import askForSmashedHead, onTag, RFID_init, Repeat, get_temperature, temperature_init

tempSensor = temperature_init()
# should be restarted after 24h (TODO)

t = Repeat(1.0, lambda: get_temperature(tempSensor))
t.start()
