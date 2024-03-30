#ifndef MARSHALLER_H
#define MARSHALLER_H

#include <string>
#include <vector>

#include "ClientPacket.h"

/**
 * @brief The Marshaller class provides static methods for marshalling and
 * unmarshalling data.
 */
class Marshaller {
   public:
    /**
     * @brief Marshals a ClientPacket object into a vector of unsigned char.
     * @param clientPacket The ClientPacket object to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshalClientPacketS1(
        const ClientPacket& clientPacket);

    /**
     * @brief Marshals a ClientPacket object into a vector of unsigned char.
     * @param clientPacket The ClientPacket object to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshalClientPacketS2(
        const ClientPacket& clientPacket);

    /**
     * @brief Marshals a ClientPacket object into a vector of unsigned char.
     * @param clientPacket The ClientPacket object to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshalClientPacketS3(
        const ClientPacket& clientPacket);

    /**
     * @brief Marshals a ClientPacket object into a vector of unsigned char.
     * @param clientPacket The ClientPacket object to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshalClientPacketS4(
        const ClientPacket& clientPacket);

    /**
     * @brief Marshals a ClientPacket object into a vector of unsigned char.
     * @param clientPacket The ClientPacket object to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshalClientPacketS5(
        const ClientPacket& clientPacket);

    /**
     * @brief Marshals an integer into a vector of unsigned char.
     * @param x The integer to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshal(int x);

    /**
     * @brief Marshals a long integer into a vector of unsigned char.
     * @param x The long integer to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshal(long long x);

    /**
     * @brief Marshals a string into a vector of unsigned char.
     * @param x The string to be marshalled.
     * @return A vector of unsigned char containing the marshalled data.
     */
    static std::vector<unsigned char> marshal(const std::string& x);

    /**
     * @brief Unmarshals an integer from a vector of char.
     * @param x The vector of char containing the data to be unmarshalled.
     * @param startIndex The starting index of the data in the vector.
     * @return The unmarshalled integer value.
     */
    static int unmarshalInt(const std::vector<char>& x, int startIndex);

    /**
     * @brief Unmarshals a long integer from a vector of char.
     * @param x The vector of char containing the data to be unmarshalled.
     * @param startIndex The starting index of the data in the vector.
     * @return The unmarshalled long integer value.
     */
    static long long unmarshalLong(const std::vector<char>& x, int startIndex);

    /**
     * @brief Unmarshals a string from a vector of char.
     * @param x The vector of char containing the data to be unmarshalled.
     * @param startIndex The starting index of the data in the vector.
     * @param length The length of the string to be unmarshalled.
     * @return The unmarshalled string value.
     */
    static std::string unmarshalString(const std::vector<char>& x,
                                       int startIndex, int length);

    /**
     * @brief Appends an integer to a vector of unsigned char.
     * @param byteArray The vector of unsigned char to which the integer will be
     * appended.
     * @param x The integer to be appended.
     * @return A new vector of unsigned char with the integer appended.
     */
    static std::vector<unsigned char> appendInt(
        const std::vector<unsigned char>& byteArray, int x);

    /**
     * @brief Appends a long integer to a vector of unsigned char.
     * @param byteArray The vector of unsigned char to which the long integer
     * will be appended.
     * @param x The long integer to be appended.
     * @return A new vector of unsigned char with the long integer appended.
     */
    static std::vector<unsigned char> appendLongLong(
        const std::vector<unsigned char>& byteArray, long long x);

    /**
     * @brief Appends a string to a vector of unsigned char.
     * @param byteArray The vector of unsigned char to which the string will be
     * appended.
     * @param s The string to be appended.
     * @return A new vector of unsigned char with the string appended.
     */
    static std::vector<unsigned char> appendString(
        const std::vector<unsigned char>& byteArray, const std::string& s);
};

#endif  // MARSHALLER_H