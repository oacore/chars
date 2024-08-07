#!/bin/bash

REMOTE_CHARS=/data/chars
LOCAL_CHARS=/data/chars


#kill scheduler process if any
kill -9 `ps -ef | grep java|grep EventScheduler | grep -v grep|awk '{print $2}'`

echo "Starting event scheduler..."

java -Djava.security.egd=file:/dev/./urandom -jar $REMOTE_CHARS/bin/EventScheduler-1.0-SNAPSHOT-shaded.jar >> $LOCAL_CHARS/logs/scheduler.log 2>&1 &