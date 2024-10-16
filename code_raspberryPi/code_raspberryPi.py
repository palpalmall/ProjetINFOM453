#  Raspberry Pi Master for Arduino Slave
#  i2c_master_pi.py
#  Connects to Arduino via I2C
  
#  DroneBot Workshop 2019
#  https://dronebotworkshop.com

from smbus import SMBus

addr1 = 0x8 # mkr1000 address
addr2 = 0x3C #uno R3 address
bus = SMBus(1) # indicates /dev/ic2-1

numb = 1

print ("Enter 1 for ON or 0 for OFF")
while numb == 1:

	ledstate = input(">>>>   ")

	if ledstate == "1":
		bus.write_byte(addr1, 0x1) # switch it on
		bus.write_byte(addr2, 0x1) # switch it on
	elif ledstate == "0":
		bus.write_byte(addr1, 0x0) # switch it off
		bus.write_byte(addr2, 0x0) # switch it off
	elif ledstate == "2":
		data = bus.read_i2c_block_data(0x3C, 0x3, 6)
		print("receiuved data", data)
	else:
		numb = 0
