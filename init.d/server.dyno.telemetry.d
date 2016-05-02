#!/bin/bash

### BEGIN INIT INFO
# Provides:          server.dyno.telemetry.d
# Required-Start:    
# Required-Stop:     
# Default-Start:     
# Default-Stop: 
# Short-Description: server.dyno.telemetry.d
# Description:       DynoCloud Server Telemetry Daemon
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
            running=$(ps -elf | grep com.dynocloud.server.telemetry.Daemon | grep -v grep | wc -l)

            if [[ $running -gt 0 ]]
            then   
                    echo "Telemetry Daemon already running"
            else

                echo "Starting Telemetry Daemon"

                if [[ $currentUser == $user ]]
                then                    
                        touch "${install_dir}logs/telemetry.log"
                        "${install_dir}DynoServerTelemetryDaemon.sh" >> "${install_dir}logs/telemetry.log" 2>&1 &
                        echo "Done."
                else
                        su $user -c "touch "${install_dir}logs/telemetry.log""
                        su $user -c ""${install_dir}DynoServerTelemetryDaemon.sh" >> "${install_dir}logs/telemetry.log" 2>&1 &"
                        echo "Done."
                fi
            fi
        ;;
        stop) 
        echo "Stopping Telemetry Daemon"
        
                pkill -f com.dynocloud.server.telemetry.Daemon

        echo "Done."
        ;;
        restart)
            echo "Restarting Telemetry Daemon"

                if [[ $currentUser == $user ]]
                then                    
                        pkill -f com.dynocloud.server.telemetry.Daemon
                        sleep 1
                        touch "${install_dir}logs/telemetry.log"
                        "${install_dir}DynoServerTelemetryDaemon.sh" >> "${install_dir}logs/telemetry.log" 2>&1 &
                else
                        pkill -f com.dynocloud.server.telemetry.Daemon
                        sleep 1
                        su $user -c "touch "${install_dir}logs/telemetry.log""
                        su $user -c ""${install_dir}DynoServerTelemetryDaemon.sh" >> "${install_dir}logs/telemetry.log" 2>&1 &"
                fi

        echo "Done."
        ;;
        *)
        echo "Usage: /etc/init.d/mosquitto start|stop|restart"
        exit 1
        ;;
esac