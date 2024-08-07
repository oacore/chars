#!/bin/bash

HOST="http://localhost:9200"
set -x
for filename in *.json; do
    [ -e "$filename" ] || continue
    FILENAME_START="$(cut -d'.' -f1 <<< $filename)"

    # Create the Index
    curl -sS -i -XPUT $HOST/$FILENAME_START > /dev/null

    # Set the Mappings
#    curl -sS -i -XPUT "$HOST/$FILENAME_START/_mapping" -H "Content-Type: application/json" --data-binary @$filename
done
