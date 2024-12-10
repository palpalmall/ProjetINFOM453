from smbus import SMBus
from Phidget22.Devices.RFID import *
from Phidget22.Devices.TemperatureSensor import *
import time, functools, threading

bus = SMBus(1) # indicates /dev/ic2-1

def activate_ping_figurine(id_figurine, dico):
  print("ping figurine %s" %str(id_figurine))
  addr = dico[id_figurine]
  #addr = int(addr, 16)
  bus.write_i2c_block_data(addr,1, [1]) # switch servo motor on
		
def activate_status_figurine(id_figurine, status, dico):
  addr =  dico[id_figurine]
  #addr = int(addr, 16)
  match status:
    case "red":
      bus.write_i2c_block_data(addr,0, [0])
			
    case "yellow":
      bus.write_i2c_block_data(addr,0, [1])
			
    case "green":
      bus.write_i2c_block_data(addr,0, [2])
			
  print("status is %s" %str(status))
		
def activate_mood_figurine(id_figurine, mood, dico):
  print("mood is %s" %str(mood))
  addr =  dico[id_figurine]
  #addr = int(addr, 16)
  message = [ord(character) for character in mood] # tranform mood message into a ascii code list
  bus.write_i2c_block_data(addr, 4, message)

# envoit aux arduinos la valeur de status pour chaque figurine
# action nbr = 0
def sendStatusToSlaves(dico, status):
	for namesAddr in dico.keys():
		try:
			if(status[namesAddr]):
				bus.write_i2c_block_data(dico[namesAddr],0, [1]) # switch yellow on
			else :
				bus.write_i2c_block_data(dico[namesAddr],0, [0]) # switch red on
		except :
			print("name %s does not exist in the addresses dico" %str(namesAddr))
			
def askForSmashedHead(dico):
	smashedHeadDico = {}
	for namesAddr in dico.keys():
		try:
			smashed = bus.read_i2c_block_data(dico[namesAddr], 2, 1)
			smashedHeadDico[namesAddr] = smashed
		except :
			print("name %s does not exist in the addresses dico" %str(namesAddr))
	return smashedHeadDico


def onTag(self, tag, protocol, param):
	split_tag = tag.split("-")
	param[split_tag[0]] = int(split_tag[1], 16)
	print("Tag : "+str(tag))
	print("Protocol : "+str(protocol))
	print(param)

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
    
from threading import Timer

class Repeat(Timer):
    def run(self):
        while not self.finished.wait(self.interval):
            self.function(*self.args, **self.kwargs)

# To give a tag to the badge
# while True: 
# 	ch.write("101112-0x8", RFIDProtocol.PROTOCOL_PHIDGETS, False)