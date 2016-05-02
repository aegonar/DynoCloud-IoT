#!/bin/bash

export DATA='{"username": "agonar","password": 1234}'

# auth=$(curl -s -X POST \
#  -H "Content-Type: application/json" \
#  -d "${DATA}" \
#  http://localhost/server_api/login)

 curl -s -X POST \
 -H "Content-Type: application/json" \
 -d "${DATA}" \
 -i http://localhost/server_api/login


# # curl -X GET \
# #  -H "Content-Type: application/json" \
# #  -H "Authorization: Bearer $auth" \
# # http://localhost/api/telemetry

#echo $auth 

# curl -X GET \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# http://localhost/api/viewUser

# # curl -X GET \
# #  -H "Content-Type: application/json" \
# # -i http://localhost/api/hello


# echo

# curl -X POST \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# -i http://localhost/api/logout

# echo

# curl -X GET \
#  -H "Content-Type: application/json" \
# -i http://dynocare.xyz/api/hello

# echo

# auth=$(curl -s -X POST \
#  -H "Content-Type: application/json" \
#  -d "${DATA}" \
#  http://dynocare.xyz/api/login)

# echo $auth

# curl -X GET \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# http://dynocare.xyz/api/viewUser

# echo

# curl -X POST \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# -i http://dynocare.xyz/api/logout

# echo
# curl -X GET \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# http://dynocare.xyz/api/telemetry

# echo


# echo

# auth=$(curl -s -X POST \
#  -H "Content-Type: application/json" \
#  -d "${DATA}" \
#  http://192.168.0.253/api/login)

# echo $auth

# curl -X GET \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# http://192.168.0.253/api/viewUser

# echo

# curl -X POST \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# http://192.168.0.253/api/logout

#export reg='{"userName": "agonar","password": 1234, "name":"Alejandro", "lastname":"Gonzalez", "email":"penis", "phone":""}'
#export reg='{"userID":"001", "userName": "agonar", "name":"Alejandro", "lastName":"Gonzalez", "email":"penis", "phone":"7873415476"}'
# export reg='{"userName": "ssss", "password":"ebin", "name":"Alejandro", "lastName":"Gonzalez", "email":"email@mail.com", "phone":"7873415476"}'


# curl -X POST \
#  -H "Content-Type: application/json" \
#  -d "${reg}" \
# -i http://localhost/server_api/register
#  echo


# export pet='{"day_Humidity_SP":50.0,"night_Temperature_SP":75.0,"night_Humidity_SP":55.0,"temperature_TH":5.0,"humidity_TH":5.0,"day_Temperature_SP":80.0,"userID":2,"name":"Pepe"}'

# curl -X POST \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
#  -d "$pet" \
# -i http://localhost/server_api/profiles

# echo

# curl -X GET \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# -i http://localhost/server_api/profiles
 
#  echo

#  curl -X GET \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# -i http://localhost/server_api/profiles/2

# echo

#  curl -X DELETE \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
# -i http://localhost/server_api/profiles/2

# echo

# export pet='{"day_Humidity_SP":50.0,"night_Temperature_SP":75.0,"night_Humidity_SP":55.0,"temperature_TH":5.0,"humidity_TH":5.0,"day_Temperature_SP":80.0,"name":"Feels"}'

#  curl -X PUT \
#  -H "Content-Type: application/json" \
#  -H "Authorization: Bearer $auth" \
#  -d "$pet" \
# -i http://localhost/server_api/profiles/2

# echo

