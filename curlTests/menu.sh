#!/bin/bash

# --- Function to create a user ---
create_user() {
    local username=$1
    local password=$2
    curl -s -X POST http://localhost:10001/users \
        --header "Content-Type: application/json" \
        -d "{\"Username\":\"$username\", \"Password\":\"$password\"}"
}

# --- Function to get a token for a user ---
get_token() {
    local username=$1
    local password=$2
    local token=$(curl -s -X POST http://localhost:10001/sessions \
        --header "Content-Type: application/json" \
        -d "{\"Username\":\"$username\", \"Password\":\"$password\"}" | \
        grep -o '"token":"[^"]*"' | sed 's/"token":"\([^"]*\)"/\1/')
    echo "$token"
}

# --- Create users and get their tokens ---
create_and_get_tokens() {
    # Create Admin User (if it doesn't exist)
    create_user "admin" "istrator"

    # Create Regular Users
    create_user "kienboec" "password1"
    create_user "altenhof" "password2"

    # Get Tokens for Users
    TOKEN_ADMIN=$(get_token "admin" "istrator")
    TOKEN_KIENBOEC=$(get_token "kienboec" "password1")
    TOKEN_ALTENHOF=$(get_token "altenhof" "password2")

    # Check if Tokens were obtained
    if [[ -z "$TOKEN_ADMIN" || -z "$TOKEN_KIENBOEC" || -z "$TOKEN_ALTENHOF" ]]; then
        echo "Error: Could not obtain all tokens. Check credentials."
        exit 1
    fi
}

# --- Function to display the menu ---
display_menu() {
    echo "------------------------"
    echo "   Card Trading App"
    echo "------------------------"
    echo "1) Register User"
    echo "2) Login User"
    echo "3) Create Packages (only possible for admin)"
    echo "4) Buy Cards from a Package"
    echo "5) Set Random Deck for Battle Test"
    echo "6) Battle"
    echo "7) Fetch and Display Cards"
    echo "8) Create Trade"
    echo "9) Fetch and Display Trades"
    echo "10) Accept Trade"
    echo "11) Fetch battle stats"
    echo "12) Exit"
    echo "------------------------"
    echo -n "Enter your choice: "
    read -r choice
}

# --- Function to select user ---
select_user() {
    echo "Select user to run the command:"
    echo "1) Admin"
    echo "2) Kienboec"
    echo "3) Altenhof"
    echo -n "Enter your choice: "
    read -r user_choice

    case "$user_choice" in
        1) user_token="$TOKEN_ADMIN" ;;
        2) user_token="$TOKEN_KIENBOEC" ;;
        3) user_token="$TOKEN_ALTENHOF" ;;
        *) echo "Invalid choice. Defaulting to Admin."
           user_token="$TOKEN_ADMIN" ;;
    esac
}

# --- Function to register a new user ---
register_user() {
    echo -n "Enter username: "
    read -r username
    echo -n "Enter password: "
    read -r password

    curl -X POST http://localhost:10001/users \
        --header "Content-Type: application/json" \
        -d "{\"Username\":\"$username\", \"Password\":\"$password\"}"

    echo
    echo "User registered. Press Enter to continue..."
    read -r
}

# --- Function to log in a user ---
login_user() {
    echo -n "Enter username: "
    read -r username
    echo -n "Enter password: "
    read -r password

    response=$(curl -s -X POST http://localhost:10001/sessions \
        --header "Content-Type: application/json" \
        -d "{\"Username\":\"$username\", \"Password\":\"$password\"}")
    echo "Response: $response"
    token=$(echo "$response" | grep -o '"token":"[^"]*"' | sed 's/"token":"\([^"]*\)"/\1/')

    if [[ -n "$token" ]]; then
        echo "Login successful!"
        echo "Your token is: $token"
        echo "export CURRENT_USER_TOKEN=\"$token\"" > user_session.sh
    else
        echo "Login failed!"
    fi

    echo "Press Enter to continue..."
    read -r
}

# --- Function to create packages (admin only) ---
create_packages() {
    pack1="./curlTests/packages/pack"
    for number in 1 2 3 4 5 6; do
        package="${pack1}${number}.json"
        curl -X POST http://localhost:10001/packages \
            --header "Content-Type: application/json" \
            --header "Authorization: Bearer $TOKEN_ADMIN" \
            -d @"$package"

        echo "Should return HTTP 201"
        echo
    done
    echo "Packages created. Press Enter to continue..."
    read -r
}

# --- Function to buy cards from a package ---
buy_cards_from_package() {
    select_user
    echo "Buying cards for the selected user..."
    curl -X POST http://localhost:10001/transactions/packages \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer $user_token" \
        -d ""
    echo "Should return HTTP 201"
    echo
    echo "Cards purchased. Press Enter to continue..."
    read -r
}

# --- Function to set random deck for battle test ---
set_random_deck() {
    select_user
    echo "Setting random deck for the selected user..."
    curl -i -X POST http://localhost:10001/deck/random \
        --header "Authorization: Bearer $user_token"
    echo
    echo "Random deck set. Press Enter to continue..."
    read -r
}

# --- Function to initiate battle ---
initiate_battle() {
    select_user
    echo "Initiating battle for the selected user..."
    curl -i -X POST http://localhost:10001/battles \
        --header "Authorization: Bearer $user_token"
    echo
    echo "Battle initiated. Press Enter to continue..."
    read -r
}

# --- Function to fetch and display cards ---
fetch_cards() {
    select_user
    response=$(curl -s -X GET http://localhost:10001/cards \
        --header "Authorization: Bearer $user_token")
    echo "Cards for the selected user:"
    echo "$response"
    echo
    echo "Press Enter to continue..."
    read -r
}

# --- Function to create a trade ---
create_trade() {
    select_user
    echo "Fetching list of cards..."
    response=$(curl -s -X GET http://localhost:10001/cards \
        --header "Authorization: Bearer $user_token")
    echo "Response: $response"

    # Extract the 'id' and 'name' fields and display them
    cards=$(echo "$response" | jq -r '.[] | "\(.id): \(.name)"')
    echo "Select a card to trade:"
    echo "$cards"
    echo -n "Enter the card ID to trade: "
    read -r card_id
    echo -n "Enter the desired type for the trade (monster/spell): "
    read -r type
    echo -n "Enter the minimum damage for the trade: "
    read -r minimum_damage

    curl -i -X POST http://localhost:10001/tradings \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer $user_token" \
        -d "{\"cardToTrade\":\"$card_id\",\"type\":\"$type\",\"minimumDamage\":$minimum_damage}"
    echo
    echo "Trade created. Press Enter to continue..."
    read -r
}

# --- Function to fetch and display trades ---
fetch_trades() {
    select_user
    response=$(curl -s -X GET http://localhost:10001/tradings \
        --header "Authorization: Bearer $user_token")
    echo "Trades for the selected user:"
    echo "$response"
    echo
    echo "Press Enter to continue..."
    read -r
}

# --- Function to accept a trade ---
accept_trade() {
    select_user
    echo "Fetching list of trades..."
    response=$(curl -s -X GET http://localhost:10001/tradings \
        --header "Authorization: Bearer $user_token")
    echo "Response: $response"

    # Extract the 'id' and other fields and display them
    trades=$(echo "$response" | jq -r '.[] | "\(.id): \(.cardToTrade) - Type: \(.type), Min Damage: \(.minimumDamage), Owner ID: \(.ownerId)"')
    echo "Select a trade to accept:"
    echo "$trades"
 response=$(curl -s -X GET http://localhost:10001/cards \
        --header "Authorization: Bearer $user_token")
    echo "Cards for the selected user:"
    echo "$response"

    echo -n "Enter the trade ID to accept: "
    read -r trade_id
    echo -n "Enter the card ID to offer: "
    read -r offered_card_id

    curl -i -X POST http://localhost:10001/tradings/$trade_id \
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer $user_token" \
        -d "{\"offeredCardId\":\"$offered_card_id\"}"
    echo
    echo "Trade accepted. Press Enter to continue..."
    read -r
}
get_battlestats()
{
select_user
response=$(curl -s -X GET http://localhost:10001/battles \
    --header "Authorization: Bearer $user_token")
echo "$response"
echo "Press enter to continue..."
read -r 
}
# --- Initialize Users and Tokens ---
create_and_get_tokens

# --- Main loop ---
while true; do
    display_menu

    case "$choice" in
        1) register_user ;;
        2) login_user ;;
        3) create_packages ;;
        4) buy_cards_from_package ;;
        5) set_random_deck ;;
        6) initiate_battle ;;
        7) fetch_cards ;;
        8) create_trade ;;
        9) fetch_trades ;;
        10) accept_trade ;;
        11) get_battlestats ;;
        12) echo "Exiting..." && exit 0 ;;
        *) echo "Invalid choice! Press Enter to continue..."
           read -r ;;
    esac
done

