#!/bin/bash
port=${1:-8080}

#NAME=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 6 | head -n 1)

curl -H "content-type: application/json" -d'{"name":"test"}' http://localhost:${port}/dummies
