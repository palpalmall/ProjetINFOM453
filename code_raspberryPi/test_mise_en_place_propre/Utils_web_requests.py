import json
import requests
from Utils_RPi_Arduino import activate_ping_figurine, activate_mood_figurine, activate_status_figurine

url = "http://localhost:8080"

#------------- GET methods --------------

def get_init(login, psw):
    params = json.dumps({'login': login, 'psw': psw})
    response = requests.get(url+'/get_init', params)
    response = json.loads(response)
    my_id = response['my_id']
    team_nbr = response['team_nbr']
    return my_id, team_nbr

def get_ping(team_nbr, my_id, url = url) :
    params = json.dumps({'team_nbr' : team_nbr, 'my_id' : my_id})
    response = requests.get(url+"/get_ping", params)
    response = json.loads(response)
    for id_figurine in response.keys():
        if(response[id_figurine]):
            activate_ping_figurine(id_figurine)

def get_status(team_nbr, my_id, url = url) :
    params = json.dumps({'team_nbr' : team_nbr, 'my_id' : my_id})
    response = requests.get(url+"/get_status", params)
    response = json.loads(response)
    for id_figurine in response.keys():
        activate_status_figurine(id_figurine, response[id_figurine])

def get_mood(team_nbr, my_id, url = url) :
    params = json.dumps({'team_nbr' : team_nbr, 'my_id' : my_id})
    response = requests.get(url+"/get_mood", params)
    response = json.loads(response)
    for id_figurine in response.keys():
        activate_mood_figurine(id_figurine, response[id_figurine])

#------------- POST methods --------------

def post_ping(team_nbr, my_id, id_fig, url = url) :
    params = json.dumps(
        {'team_nbr' : team_nbr,
         'my_id' : my_id,
         'id_fig' : id_fig})
    
    response = requests.post(url+'/post_ping',json=params)
    #check if response OK

def post_status(team_nbr, my_id, status, url = url) :
    params = json.dumps(
        {'team_nbr' : team_nbr,
         'my_id' : my_id,
         'status' : status})
    
    response = requests.post(url+'/post_status',json=params)
    #check if response OK

def post_mood(team_nbr, my_id, mood, url = url) :
    params = json.dumps(
        {'team_nbr' : team_nbr,
         'my_id' : my_id,
         'mood' : mood})
    
    response = requests.post(url+'/post_mood',json=params)
    #check if response OK

def post_temperature(team_nbr, my_id, temperature, url = url):
    params = json.dumps(
        {'team_nbr' : team_nbr,
         'my_id' : my_id,
         'temperature' : temperature})
    
    response = requests.post(url+'/post_temperature',json=params)