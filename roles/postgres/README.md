Postgres
=========

Role to pull img, start container with volume for mtcg app and resetting/purging it
Requirements
------------

ansible on mac or linux

Role Variables
--------------

image name, version as well db user,pwd and db name are set in var/main.yml

Usage
-----

```
# Full setup/ensure container is started

ansible-playbook db.yml 


# Reset DB (stop container, rm container+volume)

ansible-playbook db.yaml --tags "utils,reset"


# Full purge (container,volume,img deleted)

ansible-playbook db.yaml --tags"utils,reset,purge"

# Setup db tables

ansible-playbook db.yaml --tags utils
```




