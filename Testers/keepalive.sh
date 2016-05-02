#!/bin/bash

for i in `seq 1 10000`;
do

		timedate=$(date +"%Y-%d-%m %H:%M:%S")
        mosquitto_pub -h 192.168.0.199 -t /DynoCloud/1 -m keepalive
        echo $timedate keepalive
        sleep 10
        
 done  
