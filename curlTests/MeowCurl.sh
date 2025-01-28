#!/bin/bash

# Create Users (Registration)
echo "1) Create Users (Registration)"
curl  -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo "Should return HTTP 201"
echo
curl  -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}"
echo "Should return HTTP 201"
echo
curl  -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"admin\", \"Password\":\"istrator\"}"
echo "Should return HTTP 201"
echo

# Login Users (Sessions) and fetch tokens
echo "2) Login Users"
RESPONSE_KIENBOEC=$(curl  -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}")
echo "Response for kienboec: $RESPONSE_KIENBOEC"
TOKEN_KIENBOEC=$(echo $RESPONSE_KIENBOEC | grep -o '"token":"[^"]*"' | sed 's/"token":"\([^"]*\)"/\1/')
echo "Token for kienboec: '$TOKEN_KIENBOEC'"
echo

RESPONSE_ALTENHOF=$(curl  -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}")
echo "Response for altenhof: $RESPONSE_ALTENHOF"
TOKEN_ALTENHOF=$(echo $RESPONSE_ALTENHOF | grep -o '"token":"[^"]*"' | sed 's/"token":"\([^"]*\)"/\1/')
echo "Token for altenhof: '$TOKEN_ALTENHOF'"
echo

RESPONSE_ADMIN=$(curl  -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"admin\", \"Password\":\"istrator\"}")
echo "Response for admin: $RESPONSE_ADMIN"
TOKEN_ADMIN=$(echo $RESPONSE_ADMIN | grep -o '"token":"[^"]*"' | sed 's/"token":"\([^"]*\)"/\1/')
echo "Token for admin: '$TOKEN_ADMIN'"
echo

# Create Packages (done by admin)
echo "3) Create Packages"
pack1="./curlTests/packages/pack"
for number in 1 2 3 4 5 6;
do
    package="${pack1}${number}.json"
    curl  -X POST http://localhost:10001/packages \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer $TOKEN_ADMIN" \
        -d @$package
    
    echo "Should return HTTP 201"
    echo
done

# Buying Cards from a Package
echo "4) Buying Cards from a Package"
echo "Token for kienboec: '$TOKEN_KIENBOEC'"
curl  -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN_KIENBOEC" -d ""
echo "Should return HTTP 201"
echo

echo "Token for altenhof: '$TOKEN_ALTENHOF'"
curl  -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN_ALTENHOF" -d ""
echo "Should return HTTP 201"
echo

echo "Token for admin: '$TOKEN_ADMIN'"
curl  -X POST http://localhost:10001/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN_ADMIN" -d ""
echo "Should return HTTP 201"
echo
curl -i -X POST http://localhost:10001/deck/random --header "Authorization: Bearer $TOKEN_KIENBOEC"
curl -i -X POST http://localhost:10001/deck/random --header "Authorization: Bearer $TOKEN_ADMIN"
curl -i -X POST http://localhost:10001/battles --header "Authorization: Bearer $TOKEN_KIENBOEC"
curl -i -X POST http://localhost:10001/battles --header "Authorization: Bearer $TOKEN_ADMIN"
