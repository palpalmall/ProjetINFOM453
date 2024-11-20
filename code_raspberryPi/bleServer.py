"""
Example for a BLE 4.0 Server
"""
import sys
import logging
import asyncio
import threading

from wifiConnection import configure_wifi
from typing import Any, Union

from bless import (  # type: ignore
    BlessServer,
    BlessGATTCharacteristic,
    GATTCharacteristicProperties,
    GATTAttributePermissions,
)

test_structure = {"wifi_name" : "", "wifi_password" : ""} #Ajout Mael

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(name=__name__)

# NOTE: Some systems require different synchronization methods.
trigger: Union[asyncio.Event, threading.Event]
if sys.platform in ["darwin", "win32"]:
    trigger = threading.Event()
else:
    trigger = asyncio.Event()


def read_request(characteristic: BlessGATTCharacteristic, **kwargs) -> bytearray:
    logger.debug(f"Reading {characteristic.value}")
    return characteristic.value

def try_to_connect_wifi():
	
    print(test_structure["wifi_name"])
    print(test_structure["wifi_password"])
	
    if(test_structure["wifi_name"] != "" and test_structure["wifi_password"] != ""):
	    return configure_wifi(test_structure["wifi_name"], test_structure["wifi_password"])

def write_request(characteristic: BlessGATTCharacteristic, value: Any, **kwargs):
    characteristic.value = value
    if characteristic.value == b"\x0f":
        logger.debug("NICE")
        trigger.set()

    # Ajout Mael
    if (characteristic.uuid == "51ff12bb-3ed8-46e5-b4f9-d64e2fec021b"):
        characteristic.value = value
        logger.debug(f"wifi name is : {characteristic.value}")
        
        if(test_structure["wifi_name"] != ""):
            test_structure["wifi_name"] = str(test_structure["wifi_name"]) + characteristic.value.decode("utf-8")
        else :
            test_structure["wifi_name"] = characteristic.value.decode("utf-8")

    if(characteristic.uuid == "51ff12bb-3ed8-46e5-b4f9-d64e2fec021c") :
        characteristic.value = value
        logger.debug(f"wifi password is : {characteristic.value}")
        
        if(test_structure["wifi_password"] != ""):
            test_structure["wifi_password"] = str(test_structure["wifi_password"]) + characteristic.value.decode("utf-8")
        else :
            test_structure["wifi_password"] = characteristic.value.decode("utf-8")
    
    print(test_structure)
    response = try_to_connect_wifi()
    print("response = %s" %response)
    if(response) :
	    pass
    #Fin ajout Mael


async def run(loop):
    trigger.clear()
    # Instantiate the server
    my_server_name = "Raspberry Pi Mael"
    server = BlessServer(name=my_server_name, loop=loop)
    server.read_request_func = read_request
    server.write_request_func = write_request

    # Add Service
    my_service_uuid = "A07498CA-AD5B-474E-940D-16F1FBE7E8CD"
    await server.add_new_service(my_service_uuid)

    # Add a Characteristic to the service
    my_char_uuid_wifi_name = "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021B"
    char_flags = (
        GATTCharacteristicProperties.read
        | GATTCharacteristicProperties.write
        | GATTCharacteristicProperties.indicate
    )
    permissions = GATTAttributePermissions.readable | GATTAttributePermissions.writeable
    await server.add_new_characteristic(
        my_service_uuid, my_char_uuid_wifi_name, char_flags, None, permissions
    )
    
    my_char_uuid_wifi_password = "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021C"
    char_flags = (
        GATTCharacteristicProperties.read
        | GATTCharacteristicProperties.write
        | GATTCharacteristicProperties.indicate
    )
    permissions = GATTAttributePermissions.readable | GATTAttributePermissions.writeable
    await server.add_new_characteristic(
        my_service_uuid, my_char_uuid_wifi_password, char_flags, None, permissions
    )

    # Ajout Mael
    # mael_service_uuid = "A07498CA-AD5B-474E-940D-16F1FBE7E8CE"
    # await server.add_new_service(mael_service_uuid)

    # mael_char_uuid_wifi_name = "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021C"
    # char_flags = (
        # GATTCharacteristicProperties.read
        # | GATTCharacteristicProperties.write
        # | GATTCharacteristicProperties.indicate
    # )
    # permissions = GATTAttributePermissions.readable | GATTAttributePermissions.writeable
    # await server.add_new_characteristic(
        # mael_service_uuid, mael_char_uuid_wifi_name, char_flags, None, permissions
    # )

    # mael_char_uuid_wifi_password = "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021D"
    # char_flags = (
        # GATTCharacteristicProperties.read
        # | GATTCharacteristicProperties.write
        # | GATTCharacteristicProperties.indicate
    # )
    # permissions = GATTAttributePermissions.readable | GATTAttributePermissions.writeable
    # await server.add_new_characteristic(
        # mael_service_uuid, mael_char_uuid_wifi_password, char_flags, None, permissions
    # )
   
    # logger.debug(server.get_service(mael_service_uuid))
    # logger.debug(server.get_characteristic(mael_char_uuid_wifi_name))
    # logger.debug(server.get_characteristic(mael_char_uuid_wifi_password))
    # server.get_characteristic(mael_char_uuid_wifi_name)
    # server.get_characteristic(mael_char_uuid_wifi_password)
    # server.update_value(mael_service_uuid, "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021C")
    # server.update_value(mael_service_uuid, "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021D")
    #Fin ajout Mael

    logger.debug(server.get_characteristic(my_char_uuid_wifi_name))
    logger.debug(server.get_characteristic(my_char_uuid_wifi_password))
    await server.start()
    logger.debug("Advertising")
    logger.info(f"Write '0xF' to the advertised characteristic: {my_char_uuid_wifi_name}")
    if trigger.__module__ == "threading":
        trigger.wait()
    else:
        await trigger.wait()

    await asyncio.sleep(2)
    logger.debug("Updating")
    logger.debug(server.get_service(my_service_uuid))
    server.get_characteristic(my_char_uuid_wifi_name)
    server.update_value(my_service_uuid, "51FF12BB-3ED8-46E5-B4F9-D64E2FEC021B")

    await asyncio.sleep(5)
    await server.stop()


loop = asyncio.get_event_loop()
loop.run_until_complete(run(loop))
