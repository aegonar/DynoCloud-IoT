#!/bin/bash

### BEGIN INIT INFO
# Provides:          dyno.alert.d
# Required-Start:    
# Required-Stop:     
# Default-Start:     
# Default-Stop: 
# Short-Description: dyno.alert.d
# Description:       DynoCloud Central Node Alert Daemon
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
            running=$(ps -elf | grep com.dynocloud.node.alert.Daemon | grep -v grep | wc -l)

            if [[ $running -gt 0 ]]
            then   
                echo "Alert Daemon already running"
            else

                echo "Starting Alert Daemon"

                if [[ $currentUser == $user ]]
                then                    
                        touch "${install_dir}logs/alert.log"
                        "${install_dir}AlertDaemon_CentralNode.sh" >> "${install_dir}logs/alert.log" 2>&1 &
                        echo "Done."
                else
                        su $user -c "touch "${install_dir}logs/alert.log""
                        su $user -c ""${install_dir}AlertDaemon_CentralNode.sh" >> "${install_dir}logs/alert.log" 2>&1 &"
                        echo "Done."
                fi
            fi
        ;;
        stop) 
        echo "Stopping Alert Daemon"
        
                pkill -f com.dynocloud.node.alert.Daemon

        echo "Done."
        ;;
        restart)
            echo "Restarting Alert Daemon"

                if [[ $currentUser == $user ]]
                then                    
                        pkill -f com.dynocloud.node.alert.Daemon
                        sleep 1
                        touch "${install_dir}logs/alert.log"
                        "${install_dir}AlertDaemon_CentralNode.sh" >> "${install_dir}logs/alert.log" 2>&1 &
                else
                        pkill -f com.dynocloud.node.alert.Daemon
                        sleep 1
                        su $user -c "touch "${install_dir}logs/alert.log""
                        su $user -c ""${install_dir}AlertDaemon_CentralNode.sh" >> "${install_dir}logs/alert.log" 2>&1 &"
                fi

        echo "Done."
        ;;
        *)
        echo "Usage: dyno.alert.d start|stop|restart"
        exit 1
        ;;
esac