#!/usr/bin/env python

import asyncio
import json
from websockets.server import serve

async def echo(websocket):
    async for message in websocket:
        print("received %s" %(json.loads(message)))
        await websocket.send(message)

async def main():
    async with serve(echo, "localhost", 8765):
        await asyncio.get_running_loop().create_future()  # run forever

asyncio.run(main())