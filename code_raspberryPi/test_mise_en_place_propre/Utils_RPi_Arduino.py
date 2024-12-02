from smbus import SMBus
from Phidget22.Devices.RFID import *
from Phidget22.Devices.TemperatureSensor import *
import time, functools

bus = SMBus(1) # indicates /dev/ic2-1
addr1 = 0x8
addr2 = 0x3C

#Nom des figurines et leurs adresses arduino
NamesAddrDico = {
	
}

#Noms des figurines et leur status (false = absent, true = present)
exempleStatusReceived = {
	"101112" : True,
	"101113" : False
}


def activate_ping_figurine(id_figurine):
  print("ping figurine %d" %id_figurine)
  addr = NamesAddrDico[id_figurine]
  bus.write_i2c_block_data(addr,4, [1]) # switch servo motor on
		
def activate_status_figurine(id_figurine, status):
  addr =  NamesAddrDico[id_figurine]
  match status:
    case "occupied":
      bus.write_i2c_block_data(addr,0, [0])
			
    case "absent":
      bus.write_i2c_block_data(addr,0, [1])
			
    case "available":
      bus.write_i2c_block_data(addr,0, [2])
			
  print("status is %s" %status)
		
def activate_mood_figurine(id_figurine, mood):
  print("mood is %s" %mood)
  addr =  NamesAddrDico[id_figurine]
  message = [ord(character) for character in mood] # tranform mood message into a ascii code list
  bus.write_i2c_block_data(addr, 3, message)

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
	return smashedHeadDico


def onTag(self, tag, protocol, param):
	split_tag = tag.split("-")
	param[split_tag[0]] = split_tag[1]
	print("Tag : "+str(tag))
	print("Protocol : "+str(protocol))

def RFID_init(onTag, dico):
	ch = RFID()
	ch.openWaitForAttachment(3000)
	ch.setOnTagHandler(functools.partial(onTag, param=dico))

	antennaEnabled = ch.getAntennaEnabled()
	if(not antennaEnabled):
		ch.setAntennaEnabled(True)

	return ch

def temperature_init():
	tempSensor = TemperatureSensor()
	tempSensor.openWaitForAttachment(2000)
	return tempSensor

def get_temperature(tempSensor):
	temperature = tempSensor.getTemperature()
	print("Temperature : " + str(temperature))
    
# To give a tag to the badge
# while True: 
# 	ch.write("101112-0x8", RFIDProtocol.PROTOCOL_PHIDGETS, False)