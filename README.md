# README - CHARS #

Before doing anything else [read here](https://github.com/oacore/styleguide)

## APIWebService
See [this README](APIWebService/readme.md)

## Document download workflow
See [this README](CHARSWorkers/DocumentDownloadWorker/readme.md)

## Development
We develop against the branch `develop`. There is no master branch. We use git flow
in terms of features should appear in their own `feature/` branch and merged. Before
merging, make sure you have time to test that `develop` stays clean or don't merge!

### Data Folders
We put data and configration in /data.
In MacOS Catalina, there is not a restriction on storing files in the root directory  
The way round this is to create a folder in your home directory (or any other writable directory):

`mkdir -p ~/projects/data`

Then insert the following into `/etc/synthetic.conf`:


```
data	/Users/samuel/projects/data
```
Note: There _must_ be 1 tab between the symlink name and the path. 4 spaces doesn't seem to work. Watch out when copy and pasting!