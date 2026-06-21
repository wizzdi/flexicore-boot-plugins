#!/bin/bash

# This script generates a 2048-bit RSA private key and converts it to PKCS#8 format.
# It also extracts the public key for distribution to gateways.

# Try to find the key path from application.properties
PROPERTIES_FILE="/home/flexicore/config/application.properties"
if [ ! -f "$PROPERTIES_FILE" ]; then
    PROPERTIES_FILE="application.properties"
fi

if [ -f "$PROPERTIES_FILE" ]; then
    KEY_PATH_FROM_PROP=$(grep "^basic.iot.keyPath=" "$PROPERTIES_FILE" | cut -d'=' -f2 | tr -d '\r')
fi

if [ -z "$KEY_PATH_FROM_PROP" ]; then
    echo "Error: basic.iot.keyPath not found in $PROPERTIES_FILE"
    exit 1
fi

PKCS8_KEY_FILE="$KEY_PATH_FROM_PROP"
PUBLIC_KEY_FILE="${PKCS8_KEY_FILE%.*}.pub.pem"

# Check if file already exists and prompt for confirmation
if [ -f "$PKCS8_KEY_FILE" ]; then
    read -p "File '$PKCS8_KEY_FILE' already exists. Replace? (y/n): " confirm
    if [[ $confirm != [yY] ]]; then
        echo "Operation cancelled."
        exit 1
    fi
fi

# Create directory if it doesn't exist
TARGET_DIR=$(dirname "$PKCS8_KEY_FILE")
if [ ! -d "$TARGET_DIR" ] && [ "$TARGET_DIR" != "." ]; then
    echo "Creating directory $TARGET_DIR..."
    mkdir -p "$TARGET_DIR"
fi

PRIVATE_KEY_TEMP="temp_private.key"

echo "Generating RSA private key..."
openssl genrsa -out "$PRIVATE_KEY_TEMP" 2048

echo "Converting to PKCS#8 format..."
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in "$PRIVATE_KEY_TEMP" -out "$PKCS8_KEY_FILE"

echo "Extracting public key..."
openssl rsa -in "$PKCS8_KEY_FILE" -pubout -out "$PUBLIC_KEY_FILE"

echo "Success!"
echo "Private Key (PKCS#8): $PKCS8_KEY_FILE"
echo "Public Key:          $PUBLIC_KEY_FILE"
echo ""
echo "Note: The Public Key should be shared with Gateways so they can verify messages from the Cloud."

# Cleanup temporary original key
rm "$PRIVATE_KEY_TEMP"
