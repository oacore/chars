#!/bin/bash
#
#

# scheduler only in one server
#scp EventScheduler/target/EventScheduler-1.0-SNAPSHOT-shaded.jar la4227@core-app02:/data/chars/bin

#expect a destinations file in the same folder
for dest in $(<destinations); do
	scp EventScheduler/target/EventScheduler-1.0-SNAPSHOT-shaded.jar ${dest}:/data/chars/bin
	scp CHARSWorkers/LegacyWorker/target/LegacyWorker-1.0-SNAPSHOT-shaded.jar ${dest}:/data/chars/bin
	scp CHARSWorkers/ThumbnailGenerationWorker/target/ThumbnailGenerationWorker-1.0-SNAPSHOT.jar ${dest}:/data/chars/bin
	scp Supervisor/target/Supervisor-1.0-SNAPSHOT-shaded.jar ${dest}:/data/chars/bin
done