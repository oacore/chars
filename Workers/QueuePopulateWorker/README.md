To execute :
```
java -jar PopulateQueue-1.0-SNAPSHOT-shaded.jar --queue=<queue_name> --from=1 --to=1000
```
e.g. for populating grobid extract queue:
```
java -jar PopulateQueue-1.0-SNAPSHOT-shaded.jar --queue=grobid-extraction-item --from=1 --to=1000000
```
queue_name from Enum TaskType (uk.ac.core.common.model.task.TaskType)
Another mode is by input file (csv file one core_id per line):
```
java -jar PopulateQueue-1.0-SNAPSHOT-shaded.jar --queue=grobid-extraction-item --input-file-path=dataset.csv
```