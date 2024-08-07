#!/bin/bash
#
#

# add the ampersand (&) to run in a separate process
COMMAND=$1" &"

# log time and command
echo -e "`date` $COMMAND ">/data/chars/worker-creator.log

# execute command
eval $COMMAND