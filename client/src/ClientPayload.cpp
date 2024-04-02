/**
 * @file ClientPayload.cpp
 * @brief Implementation file for the ClientPayload class.
 */

#include "ClientPayload.h"

// Constructor
ClientPayload::ClientPayload() {}

/**
 * @brief Constructor for ClientPayload class.
 * @param expirationTime The expiration time value to set.
 */
ClientPayload::ClientPayload(unsigned long long expirationTime)
    : expirationTime(expirationTime) {}

/**
 * @brief Constructor for ClientPayload class.
 * @param offset The offset value to set.
 * @param numBytes The number of bytes to set.
 */
ClientPayload::ClientPayload(int offset, int numBytes)
    : offset(offset), numBytes(numBytes) {}

/**
 * @brief Constructor for ClientPayload class.
 * @param offset The offset value to set.
 * @param input The input string to set.
 */
ClientPayload::ClientPayload(int offset, const std::string& input)
    : offset(offset), input(input) {}

/**
 * @brief Get the offset value of the client payload.
 * @return The offset value.
 */
int ClientPayload::getOffset() const { return offset; }

/**
 * @brief Get the number of bytes of the client payload.
 * @return The number of bytes.
 */
int ClientPayload::getNumBytes() const { return numBytes; }

/**
 * @brief Get the expiration time of the client payload.
 * @return The expiration time.
 */
unsigned long long ClientPayload::getExpirationTime() const {
    return expirationTime;
}

/**
 * @brief Get the input string of the client payload.
 * @return The input string.
 */
const std::string& ClientPayload::getInput() const { return input; }

// Setters

/**
 * @brief Set the offset value of the client payload.
 * @param offset The offset value to set.
 */
void ClientPayload::setOffset(int offset) { this->offset = offset; }

/**
 * @brief Set the number of bytes of the client payload.
 * @param numBytes The number of bytes to set.
 */
void ClientPayload::setNumBytes(int numBytes) { this->numBytes = numBytes; }

/**
 * @brief Set the expiration time of the client payload.
 * @param expirationTime The expiration time to set.
 */
void ClientPayload::setExpirationTime(unsigned long long expirationTime) {
    this->expirationTime = expirationTime;
}

/**
 * @brief Set the input string of the client payload.
 * @param input The input string to set.
 */
void ClientPayload::setInput(const std::string& input) { this->input = input; }