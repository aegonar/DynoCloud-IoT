#!/bin/bash

export HT_OR=0

for i in `seq 1 10000`;
do
        mosquitto_pub -h 192.168.0.199 -t /DynoCloud/VariableRefresh -m "{\"HEAT_OR\":$HT_OR, \"HEAT_STATUS\":1}"
		
		: $((HT_OR = $HT_OR ^ 1))
		echo $HT_OR
	    sleep 1

 done