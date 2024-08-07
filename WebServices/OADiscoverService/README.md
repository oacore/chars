# OA Disocvery Service 


## Parallel discovery

### CORE discovery
We hit Elasticsearch's /discovery index with the given input

### Unpaywall
We hit unpaywall's API: 
```
http://api.unpaywall.org/v2/  [DOI]  ?email=theteam@core.ac.uk
```

### Kopernio
Kopernio's API:
```
http://canaryhaz.com/api/v1/resolve/ [DOI] 
```

Are we sure we want to hit this 1000s of times per day? are we allowed to do so ?

**Update:**
Kopernio API is not any longer publicly available, it requires authentication token.

### OA Button

Open Access Button's API endpoint:
```
https://api.openaccessbutton.org/availability?doi= [DOI]
```
e.g.
```
https://api.openaccessbutton.org/availability?doi=10.1016/j.jcp.2012.09.008
```