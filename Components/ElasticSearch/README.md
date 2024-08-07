# Create indices mappings

## Journals
To create journal index:
```
curl -XPUT "http://core-index02:9200/journals" -H 'Content-Type: application/json' -d'
{
  "settings": {
    "number_of_shards": 5
  },
  "mappings": {
    "journal": {
      "properties": {
        "identifiers": {
          "type": "keyword"
        },
        "language": {
          "type": "keyword"
        },
        "numberOfPublications": {
          "type": "long"
        },
        "publisher": {
          "type": "keyword"
        },
        "repositoryId": {
          "type": "long"
        },
        "rights": {
          "type": "keyword"
        },
        "subjects": {
          "type": "keyword"
        },
        "title": {
          "type": "text"
        }
      }
    }
  }
}'
```

## Repositories
```
curl -XPUT "http://core-index02:9200/repositories" -H 'Content-Type: application/json' -d'
{
    "settings":{
        "number_of_shards": 5
    },
    "repositories": {
        "mappings": {
            "apiDataProvider": {
                "properties": {
                    "created_date": {
                        "type": "date",
                        "format": "date_optional_time"
                    },
                    "description": {
                        "type": "text"
                    },
                    "disabled": {
                        "type": "boolean"
                    },
                    "id": {
                        "type": "long"
                    },
                    "journal": {
                        "type": "boolean"
                    },
                    "metadataFormat": {
                        "type": "keyword"
                    },
                    "name": {
                        "type": "text",
                        "fields": {
                            "raw": {
                                "type": "keyword"
                            }
                        }
                    },
                    "openDoarId": {
                        "type": "long"
                    },
                    "dataProviderLocation": {
                        "type": "nested",
                        "properties": {
                            "countryCode": {
                                "type": "keyword"
                            },
                            "id": {
                                "type": "long"
                            },
                            "latitude": {
                                "type": "double"
                            },
                            "longitude": {
                                "type": "double"
                            }
                        }
                    },
                    "roarID": {
                        "type": "long"
                    },
                    "software": {
                        "type": "keyword"
                    },
                    "source": {
                        "type": "keyword"
                    },
                    "uri": {
                        "type": "keyword"
                    },
                    "urlHomepage": {
                        "type": "keyword"
                    },
                    "urlOaipmh": {
                        "type": "keyword"
                    }
                }
            }
        }
    }
}'
```


# Export/Import data

We are using [elasticsearch-dump](https://github.com/taskrabbit/elasticsearch-dump) tool to transfer data from ES-1.7 cluster to ES6 cluster:
## Journals
```
./bin/elasticdump --input=http://core-index01.open.ac.uk:9200/journals --output=journals.json
./bin/elasticdump --output=http://core-index02.open.ac.uk:9200/journals --input=journals.json
```

## Repositories
```
./bin/elasticdump --input=http://core-index01.open.ac.uk:9200/repositories --output=repositories.json
./bin/elasticdump --output=http://core-index02.open.ac.uk:9200/ --input=repositories.json
```