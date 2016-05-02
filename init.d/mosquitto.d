#!/bin/bash

### BEGIN INIT INFO
# Provides:          mosquitto.d
# Required-Start:    
# Required-Stop:     
# Default-Start:     
# Default-Stop: 
# Short-Description: mosquitto.d
# Description:       Mosquitto MQTT Daemon
### END INIT INFO

host=$(hostname)

if [[ "$host" == "dynocloud" ]]
then                    
           install_dir="/home/dyno/DynoCloud/Daemons/"
           user="dyno"
else
        if [[ "$host" == "raspberrypi" ]]
        then                    
                install_dir="/home/pi/DynoCloud/Daemons/"
                user="pi"

        else
                if [[ "$host" == "AEGONAR-G750JX" ]]
                then                    
                        install_dir="/home/agonar/DynoCloud/Git/DynoCloud/Daemons/"
                        user="agonar"

                else
                        echo "Unknow Server host, program location path is unavailable."
                        exit 1
                fi
        fi
fi
	       
        currentUser=$(whoami)

case "$1" in
	start)
		echo "Starting mosquitto"

        	if [[ $currentUser == $user ]]
                then                    
			        touch "${install_dir}logs/mosquitto.log"
		        	mosquitto >> "${install_dir}logs/mosquitto.log" 2>&1 &
                else
			        su $user -c "touch "${install_dir}logs/mosquitto.log""
			        su $user -c "mosquitto >> "${install_dir}logs/mosquitto.log" 2>&1 &"
        	fi

        echo "Done."
        ;;
	stop) 
        echo "Stopping mosquitto"
        
        	if [[ $currentUser == $user ]]
                then                    
					killall mosquitto
                else
					su $user -c "killall mosquitto"
        	fi

        echo "Done."
        ;;
	restart)
	    echo "Restarting mosquitto"

        	if [[ $currentUser == $user ]]
                then                    
				    killall mosquitto
				    sleep 1
			        touch "${install_dir}logs/mosquitto.log"
			        mosquitto >> "${install_dir}logs/mosquitto.log" 2>&1 &
                else
				    su $user -c "killall mosquitto"
				    sleep 1
			        su $user -c "touch "${install_dir}logs/mosquitto.log""
			        su $user -c "mosquitto >> "${install_dir}logs/mosquitto.log" 2>&1 &"
        	fi

        echo "Done."
        ;;
	*)
        echo "Usage: /etc/init.d/mosquitto start|stop|restart"
        exit 1
        ;;
esac