from smbus import SMBus

bus = SMBus(1) # indicates /dev/ic2-1
addr1 = 0x8
addr2 = 0x3C

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


def activate_ping_figurine(id_figurine):
  print("ping figurine %d" %id_figurine)
  bus.write_i2c_block_data(addr1,4, [1]) # switch servo motor on
		
def activate_status_figurine(id_figurine, status):
  match status:
    case "occupied":
      bus.write_i2c_block_data(id_figurine,0, [0])
			
    case "absent":
      bus.write_i2c_block_data(id_figurine,0, [1])
			
    case "available":
      bus.write_i2c_block_data(id_figurine,0, [2])
			
  print("status is %s" %status)
		
def activate_mood_figurine(id_figurine, mood):
  print("mood is %s" %mood)

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