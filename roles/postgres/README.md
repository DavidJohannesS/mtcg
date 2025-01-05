Postgres
=========

Role to manage postgres DB

Requirements
------------

ansible on mac or linux

Control-Node needs the following python3 libraries for postgrs module to work

python3-psycopg2

```sh
# Debian based
sudo apt install -y python3-psycopg2
sudo nala install -y python3-psycopg2
```

```sh
# Rhel based 
sudo dnf install -y python3-psycopg2
```

Role Variables
--------------

image name, version as well db user,pwd and db name are set in var/main.yml

Usage
-----

```sh
# Full setup/ensure container is started

ansible-playbook db.yml 


# Reset DB (stop container, rm container+volume)

ansible-playbook db.yaml --tags "utils,reset"


# Full purge (container,volume,img deleted)

ansible-playbook db.yaml --tags"utils,reset,purge"

# Setup db tables

ansible-playbook db.yaml --tags utils
```
