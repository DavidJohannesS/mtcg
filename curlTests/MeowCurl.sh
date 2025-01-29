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
# Set random deck for battle test
curl -i -X POST http://localhost:10001/deck/random --header "Authorization: Bearer $TOKEN_KIENBOEC"
curl -i -X POST http://localhost:10001/deck/random --header "Authorization: Bearer $TOKEN_ADMIN"
# Battle
curl -i -X POST http://localhost:10001/battles --header "Authorization: Bearer $TOKEN_KIENBOEC"
curl -i -X POST http://localhost:10001/battles --header "Authorization: Bearer $TOKEN_ADMIN"
#Trading 
curl -i -X GET http://localhost:10001/cards --header "Authorization: Bearer $TOKEN_ADMIN"
curl -i -X GET http://localhost:10001/tradings --header "Authorization: Bearer $TOKEN_ADMIN"
#
#
# Fetch and display the list of cards for the admin user to select from
echo "Fetching list of cards for admin..."
cards_admin_response=$(curl -s -X GET http://localhost:10001/cards \
  --header "Authorization: Bearer $TOKEN_ADMIN")
echo "Response: $cards_admin_response"

# Extract the 'message' field content
cards_admin_message=$(echo "$cards_admin_response" | sed 's/^.*"message":"\(.*\)"}$/\1/')

# Replace escaped quotes with actual quotes
cards_admin_message=$(echo "$cards_admin_message" | sed 's/\\"/"/g')

# Remove leading and trailing square brackets
cards_admin_message=$(echo "$cards_admin_message" | sed 's/^\[\(.*\)\]$/\1/')

# Split the cards into individual entries based on '},{'
cards_admin_message=$(echo "$cards_admin_message" | sed 's/},{/}\n{/g')

# Initialize arrays to hold card IDs and names
declare -a admin_card_ids
declare -a admin_card_names

echo "Select a card to trade from admin's cards:"

index=1
while read -r card_json; do
  # Extract 'id' field
  id=$(echo "$card_json" | grep -o '"id":"[^"]*"' | cut -d':' -f2 | tr -d '"')
  # Extract 'name' field
  name=$(echo "$card_json" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')

  if [[ -n "$id" && -n "$name" ]]; then
    admin_card_ids+=("$id")
    admin_card_names+=("$name")
    echo "$index) $name (ID: $id)"
    ((index++))
  fi
done <<< "$cards_admin_message"

# Check if any cards were found
if [ "${#admin_card_ids[@]}" -eq 0 ]; then
  echo "No cards found. Exiting..."
  exit 1
fi

# Prompt the admin to select a card
read -p "Enter the number of the card admin wants to trade: " card_choice_admin
if ! [[ "$card_choice_admin" =~ ^[0-9]+$ ]] || [ "$card_choice_admin" -lt 1 ] || [ "$card_choice_admin" -gt "${#admin_card_ids[@]}" ]]; then
  echo "Invalid selection. Exiting..."
  exit 1
fi

selected_card_admin="${admin_card_ids[$((card_choice_admin-1))]}"

# Create a trade using the selected card
read -p "Enter the desired type for the trade (monster/spell): " type
read -p "Enter the minimum damage for the trade: " minimum_damage

if [[ "$type" != "monster" && "$type" != "spell" ]]; then
  echo "Invalid type. Type must be 'monster' or 'spell'. Exiting..."
  exit 1
fi

if ! [[ "$minimum_damage" =~ ^[0-9]+(\.[0-9]+)?$ ]]; then
  echo "Invalid minimum damage. Please enter a positive number. Exiting..."
  exit 1
fi

echo "Creating trade..."
curl -i -X POST http://localhost:10001/tradings \
  --header "Content-Type: application/json" \
  --header "Authorization: Bearer $TOKEN_ADMIN" \
  -d "{
        \"cardToTrade\": \"$selected_card_admin\",
        \"type\": \"$type\",
        \"minimumDamage\": $minimum_damage
      }"


# Fetch and display the list of trades
echo "Fetching list of trades..."
trades_response=$(curl -s -X GET http://localhost:10001/tradings --header "Authorization: Bearer $TOKEN_KIENBOEC")
echo "Response: $trades_response"

# Extract the 'message' field content
trades_message=$(echo "$trades_response" | sed 's/^.*"message":"\(.*\)"}$/\1/')

# Replace escaped quotes with actual quotes
trades_message=$(echo "$trades_message" | sed 's/\\"/"/g')

# Remove leading and trailing square brackets
trades_message=$(echo "$trades_message" | sed 's/^\[\(.*\)\]$/\1/')

# Split the trades into individual entries based on '},{'
trades_message=$(echo "$trades_message" | sed 's/},{/}\n{/g')

# Initialize arrays to hold trade IDs and details
declare -a trade_ids
declare -a trade_details

echo "Select a trade to accept:"

index=1
while read -r trade_json; do
  # Extract 'id' field
  id=$(echo "$trade_json" | grep -o '"id":"[^"]*"' | cut -d':' -f2 | tr -d '"')
  # Extract 'cardToTrade' and other fields for display purposes
  cardToTrade=$(echo "$trade_json" | grep -o '"cardToTrade":"[^"]*"' | cut -d':' -f2 | tr -d '"')
  type=$(echo "$trade_json" | grep -o '"type":"[^"]*"' | cut -d':' -f2 | tr -d '"')
  minimumDamage=$(echo "$trade_json" | grep -o '"minimumDamage":[^,]*' | cut -d':' -f2)
  ownerId=$(echo "$trade_json" | grep -o '"ownerId":[^}]*' | cut -d':' -f2)

  if [[ -n "$id" && -n "$cardToTrade" && -n "$type" && -n "$minimumDamage" && -n "$ownerId" ]]; then
    trade_ids+=("$id")
    trade_details+=("Card to Trade: $cardToTrade, Type: $type, Minimum Damage: $minimumDamage, Owner ID: $ownerId")
    echo "$index) $trade_details (ID: $id)"
    ((index++))
  fi
done <<< "$trades_message"

# Check if any trades were found
if [ "${#trade_ids[@]}" -eq 0 ]; then
  echo "No trades found. Exiting..."
  exit 1
fi

# Prompt the user to select a trade
read -p "Enter the number of the trade you want to accept: " trade_choice
if ! [[ "$trade_choice" =~ ^[0-9]+$ ]] || [ "$trade_choice" -lt 1 ] || [ "$trade_choice" -gt "${#trade_ids[@]}" ]; then
  echo "Invalid selection. Exiting..."
  exit 1
fi

selected_trade_id="${trade_ids[$((trade_choice-1))]}"
# Fetch and display the list of cards for the other user to select from
echo "Fetching list of cards for the other user..."
cards_other_response=$(curl -s -X GET http://localhost:10001/cards --header "Authorization: Bearer $TOKEN_KIENBOEC")
echo "Response: $cards_other_response"

# Extract the 'message' field content
cards_other_message=$(echo "$cards_other_response" | sed 's/^.*"message":"\(.*\)"}$/\1/')

# Replace escaped quotes with actual quotes
cards_other_message=$(echo "$cards_other_message" | sed 's/\\"/"/g')

# Remove leading and trailing square brackets
cards_other_message=$(echo "$cards_other_message" | sed 's/^\[\(.*\)\]$/\1/')

# Split the cards into individual entries based on '},{'
cards_other_message=$(echo "$cards_other_message" | sed 's/},{/}\n{/g')

# Initialize arrays to hold card IDs and names
declare -a other_card_ids
declare -a other_card_names

echo "Select a card to trade from the other user's cards:"

index=1
while read -r card_json; do
  # Extract 'id' field
  id=$(echo "$card_json" | grep -o '"id":"[^"]*"' | cut -d':' -f2 | tr -d '"')
  # Extract 'name' field
  name=$(echo "$card_json" | grep -o '"name":"[^"]*"' | cut -d':' -f2 | tr -d '"')

  if [[ -n "$id" && -n "$name" ]]; then
    other_card_ids+=("$id")
    other_card_names+=("$name")
    echo "$index) $name (ID: $id)"
    ((index++))
  fi
done <<< "$cards_other_message"

# Check if any cards were found
if [ "${#other_card_ids[@]}" -eq 0 ]; then
  echo "No cards found. Exiting..."
  exit 1
fi

# Prompt the other user to select a card
read -p "Enter the number of the card the other user wants to offer: " card_choice_other
if ! [[ "$card_choice_other" =~ ^[0-9]+$ ]] || [ "$card_choice_other" -lt 1 ] || [ "$card_choice_other" -gt "${#other_card_ids[@]}" ]]; then
  echo "Invalid selection. Exiting..."
  exit 1
fi

selected_card_other="${other_card_ids[$((card_choice_other-1))]}"
# Make the POST request to accept the trade
echo "Accepting trade..."
curl -i -X POST http://localhost:10001/tradings/$selected_trade_id \
  --header "Content-Type: application/json" \
  --header "Authorization: Bearer $TOKEN_KIENBOEC" \
  -d "{
        \"offeredCardId\": \"$selected_card_other\"
      }"

