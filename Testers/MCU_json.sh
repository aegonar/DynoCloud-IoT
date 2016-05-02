#!/bin/bash

for i in `seq 1 10000`;
do

export temp_SP=$(echo $RANDOM % 100 + 1| bc)
export RH_SP=$(echo $RANDOM % 100 + 1 | bc)

#export payload="{\"temp_SP\":\"$temp_SP\",\"RH_SP\":\"$RH_SP\",\"IR\":\"1\",\"IC\":\"1\",\"UV\":\"1\",\"HUM\":\"1\",\"dayTime\":\"1\",\"IC_OW\":\"0\",\"IR_OW\":\"0\",\"UV_OW\":\"0\",\"HUM_OW\":\"0\",\"IR_LAMP\":\"1\"}"

export payload="{\"temp_SP\":\"$temp_SP\",\"RH_SP\":\"$RH_SP\",\"IR\":\"0\",\"IC\":\"0\",\"UV\":\"0\",\"HUM\":\"0\",\"dayTime\":\"0\",\"IC_OW\":\"1\",\"IR_OW\":\"1\",\"UV_OW\":\"1\",\"HUM_OW\":\"1\",\"IR_LAMP\":\"0\"}"


# export payload="{\"IR\":\"1\",\"IC\":\"1\",\"UV\":\"1\",\"HUM\":\"1\",\"IC_OW\":\"0\",\"IR_OW\":\"0\",\"UV_OW\":\"0\",\"HUM_OW\":\"0\"}"


# echo $payload #| wc -m
          mosquitto_pub -h localhost -t /DynoCloud/VariableRefresh -m $payload
          sleep 0.25
#done  

#export payload="{\"temp_SP\":\"$temp_SP\",\"RH_SP\":\"$RH_SP\",\"dayTime\":\"1\",\"IR_LAMP\":\"1\"}"
#export payload="{\"temp_SP\":\"$temp_SP\",\"RH_SP\":\"$RH_SP\"}"
#export payload="{\"dayTime\":\"1\",\"IR_LAMP\":\"1\"}"
#echo $payload #| wc -m

        #mosquitto_pub -h localhost -t /DynoCloud/VariableRefresh -m $payload

 done