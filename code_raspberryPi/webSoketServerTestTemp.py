#!/usr/bin/env python

import asyncio
import json
from websockets.server import serve

connected_clients = []


async def echo(websocket):
    
    #connection_id = 0
    #connected_clients[str(connection_id)] = websocket
    #connection_id += 1
    if(websocket not in connected_clients):
        connected_clients.append(websocket)
    print(connected_clients)

    async for message in websocket:
        print("received %s" %(json.loads(message)))
        await websocket.send(message)

async def main():
    async with serve(echo, "localhost", 8765):
        await asyncio.get_running_loop().create_future()  # run forever

asyncio.run(main())