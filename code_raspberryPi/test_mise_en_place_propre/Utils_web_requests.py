import json
import requests, asyncio
#from Utils_RPi_Arduino import activate_ping_figurine, activate_mood_figurine, activate_status_figurine

url = "http://localhost:8080"

#------------- GET methods --------------

def get_init(login, psw):
    params = json.dumps({'login': login, 'psw': psw})
    response = requests.get(url+'/get_init', params)
    response = json.loads(response)
    member_id = response['member_id']
    team_id = response['team_id']
    return member_id, team_id

def get_ping(team_id, member_id, url = url) :
    response = requests.get(url+"/ping/"+str(team_id)+"/"+str(member_id))
    response = json.loads(response.text)
    print(response)
    for id_figurine in response.keys():
        if(response[id_figurine]):
            pass#activate_ping_figurine(id_figurine)

def get_status(team_id, member_id, url = url) :
    response = requests.get(url+"/status/"+str(team_id)+"/"+str(member_id))
    response = json.loads(response.text)
    for id_figurine in response.keys():
        pass#activate_status_figurine(id_figurine, response[id_figurine])

def get_mood(team_id, member_id, url = url) :
    response = requests.get(url+"/mood/"+str(team_id)+"/"+str(member_id))
    response = json.loads(response.text)
    for id_figurine in response.keys():
        pass#activate_mood_figurine(id_figurine, response[id_figurine])

#------------- POST methods --------------

def post_ping(team_id, from_fig, to_fig, url = url) : 
    response = requests.post(url+'/ping/'+str(team_id)+'/'+str(from_fig)+'/'+str(to_fig))
    print(json.loads(response.text))
    #check if response OK

def post_status(team_id, member_id, status, url = url) :
    response = requests.post(url+'/status/'+str(team_id)+'/'+str(member_id)+'/'+str(status))
    print(json.loads(response.text))
    #check if response OK

def post_mood(team_id, member_id, mood, url = url) :
    response = requests.post(url+'/mood/'+str(team_id)+'/'+str(member_id)+'/'+str(mood))
    print(json.loads(response.text))
    #check if response OK

def post_temperature(team_id, member_id, temperature, url = url):
    response = requests.post(url+'/temperature/'+str(team_id)+'/'+str(member_id)+'/'+str(temperature))
    print(json.loads(response.text))

def test_url(url, team_id = 0, member_id = 0):
    #params = json.dumps({'team_id' : team_id, 'member_id' : member_id})
    response = requests.get(url)
    print(response.content)
    response = json.loads(response.text)
    print(response)

post_ping(0, 0, 1)
#post_status(0, 0, "available")
#post_mood(0,0, "happy")
#get_ping(0,0)
#get_status(0,0)
#get_mood(0,0)