#  Raspberry Pi Master for Arduino Slave
#  i2c_master_pi.py
#  Connects to Arduino via I2C
  
#  DroneBot Workshop 2019
#  https://dronebotworkshop.com

import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BOARD)

GPIO.setup(11, GPIO.IN)
GPIO.setup(13, GPIO.IN)
GPIO.setup(15, GPIO.IN)

NamesAddrDico = {
	"Josephine" : 0x8,# mkr1000 address
	"Richard" : 0x3C #uno R3 address
}

exempleStatusReceived = {
	"Josephine" : False,
	"Richard" : False
}

def sendStatusToSlaves(NamesAddrDico, status):
	for namesAddr in NamesAddrDico.keys():
		try:
			if(status[namesAddr]):
				bus.write_i2c_block_data(NamesAddrDico[namesAddr],0, [0, 1]) # switch yellow on
			else :
				bus.write_i2c_block_data(NamesAddrDico[namesAddr],0, [0, 0]) # switch red on
		except :
			print("name %s does not exist in the addresses dico" %(namesAddr))
			

def sendJoyStickDataToSlaves():
	print()
	
def readJoyStickData():
	print()
				
		

from smbus import SMBus

bus = SMBus(1) # indicates /dev/ic2-1
addr1 = 0x8
addr2 = 0x3C

numb = 1

print ("Enter 1 for ON or 0 for OFF")
while numb == 1:
	
	print(GPIO.input(11))
	print(GPIO.input(13))
	print(GPIO.input(15))

	#ledstate = input(">>>>   ")
	ledstate = 45

	if ledstate == "1":
		bus.write_i2c_block_data(addr1,0, [1, 1]) # switch it on
		bus.write_i2c_block_data(addr2,0, [1, 1]) # switch it on
	elif ledstate == "0":
		bus.write_i2c_block_data(addr1,0, [1, 0]) # switch it off
		bus.write_i2c_block_data(addr2,0, [1, 0]) # switch it off
	elif ledstate == "2":
		data = bus.read_i2c_block_data(0x3C, 0x3, 6)
		print("receiuved data", data)
	elif ledstate == "3":
		sendStatusToSlaves(NamesAddrDico, exempleStatusReceived)
	else:
		#numb = 0
		lol = 0
