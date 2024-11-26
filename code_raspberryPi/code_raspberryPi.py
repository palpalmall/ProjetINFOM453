#  Raspberry Pi Master for Arduino Slave
#  i2c_master_pi.py
#  Connects to Arduino via I2C
  
#  DroneBot Workshop 2019
#  https://dronebotworkshop.com
import time

#Je propose que l'on fonctionne par numero d'action, ici :
# write_i2c_block_data( adresse arduino, action (le numero lier a l'action que l'on demande), ...data for action]) 
# action nbr 0 = status (absent/present)
# action nbr 1 = LED onboard
# action nbr 2 = smashed head
# action nbr 3 = mood
# action nbr 4 = servo motor

#Lorsque vous lancez le code python, l'on vous demande un chiffre : 
# tapez 0 => action nbr 1 (eteind LED onboard)
# tapez 1 => action nbr 1 (allume LED onboard)
# tapez 2 => action nbr 3 (recoit du txt)
# tapez 3 => action nbr 0 (allume LED status)
# tapez 4 => action nbr 2 (demande si la tete a été frappée)
# tapez 5 => action nbr 4 (demarre le servo motor) 

#Nom des figurines et leurs adresses arduino
NamesAddrDico = {
	"Josephine" : 0x8,# mkr1000 address
	"Richard" : 0x3C, #uno R3 address
	
}

#Noms des figurines et leur status (false = absent, true = present)
exempleStatusReceived = {
	"Josephine" : True,
	"Richard" : False
}

# envoit aux arduinos la valeur de status pour chaque figurine
# action nbr = 0
def sendStatusToSlaves(NamesAddrDico, status):
	for namesAddr in NamesAddrDico.keys():
		try:
			if(status[namesAddr]):
				bus.write_i2c_block_data(NamesAddrDico[namesAddr],0, [1]) # switch yellow on
			else :
				bus.write_i2c_block_data(NamesAddrDico[namesAddr],0, [0]) # switch red on
		except :
			print("name %s does not exist in the addresses dico" %(namesAddr))
			
def askForSmashedHead(NamesAddrDico):
	smashedHeadDico = {}
	for namesAddr in NamesAddrDico.keys():
		try:
			smashed = bus.read_i2c_block_data(NamesAddrDico[namesAddr], 2, 1)
			smashedHeadDico[namesAddr] = smashed
		except :
			print("name %s does not exist in the addresses dico" %(namesAddr))
				
	#send the dico to the server 
	print(smashedHeadDico)
	
	
from smbus import SMBus

bus = SMBus(1) # indicates /dev/ic2-1
addr1 = 0x8
addr2 = 0x3C

numb = 1

print ("Enter 1 for ON or 0 for OFF")
while numb == 1:

	ledstate = input(">>>>   ")

	if ledstate == "1":# action nbr = 1
		bus.write_i2c_block_data(addr1,1, [1]) # switch it on
		bus.write_i2c_block_data(addr2,1, [1]) # switch it on
	elif ledstate == "0":# action nbr = 1
		bus.write_i2c_block_data(addr1,1, [0]) # switch it off
		bus.write_i2c_block_data(addr2,1, [0]) # switch it off
	elif ledstate == "2":
		data = bus.read_i2c_block_data(0x8, 3, 5)
		print("receiuved data", data)
	elif ledstate == "3":
		sendStatusToSlaves(NamesAddrDico, exempleStatusReceived)
	elif ledstate == "4":
		askForSmashedHead(NamesAddrDico)
	elif ledstate == "5":
		bus.write_i2c_block_data(addr1,4, [1]) # switch servo motor on
	else:
		numb = 0
