#ifndef CLIENT_PAYLOAD_H
#define CLIENT_PAYLOAD_H

#include <string>

/**
 * @brief The ClientPayload class represents the payload sent by the client to
 * the server.
 *
 * It contains information such as the offset, number of bytes, monitor
 * interval, and input data.
 */
class ClientPayload {
    int offset;
    int numBytes;
    unsigned long long expirationTime;  // in epoch
    std::string input;

   public:
    /**
     * @brief Default constructor for the ClientPayload class.
     */
    ClientPayload();

    /**
     * @brief Constructor for the ClientPayload class with a specified monitor
     * interval.
     *
     * @param expirationTime The expiration time in minutes.
     */
    ClientPayload(unsigned long long expirationTime);

    /**
     * @brief Constructor for the ClientPayload class with a specified offset
     * and number of bytes.
     *
     * @param offset The offset value.
     * @param numBytes The number of bytes.
     */
    ClientPayload(int offset, int numBytes);

    /**
     * @brief Constructor for the ClientPayload class with a specified offset
     * and input data.
     *
     * @param offset The offset value.
     * @param input The input data.
     */
    ClientPayload(int offset, const std::string& input);

    /**
     * @brief Get the offset value.
     *
     * @return The offset value.
     */
    int getOffset() const;

    /**
     * @brief Get the number of bytes.
     *
     * @return The number of bytes.
     */
    int getNumBytes() const;

    /**
     * @brief Get the expiration time in minutes.
     *
     * @return The expiration time in minutes.
     */
    unsigned long long getExpirationTime() const;

    /**
     * @brief Get the input data.
     *
     * @return The input data.
     */
    const std::string& getInput() const;

    /**
     * @brief Set the offset value.
     *
     * @param offset The offset value to set.
     */
    void setOffset(int offset);

    /**
     * @brief Set the number of bytes.
     *
     * @param numBytes The number of bytes to set.
     */
    void setNumBytes(int numBytes);

    /**
     * @brief Set the input data.
     *
     * @param input The input data to set.
     */
    void setInput(const std::string& input);

    /**
     * @brief Set the expiration time in minutes.
     *
     * @param expirationTime The expiration time in minutes to set.
     */
    void setExpirationTime(unsigned long long expirationTime);
};

#endif  // CLIENT_PAYLOAD_H