## SC4051: Distributed Systems Socket Programming

## Marshalling of Client Packets

### `marshalClientPacketS1`

This function marshals a `ClientPacket` into a byte array with the following format:

1. `requestId` (4 bytes): The ID of the request.
2. `serviceId` (4 bytes): The ID of the service.
3. `filepath length` (4 bytes): The length of the filepath.
4. `filepath` (variable length): The filepath itself.
5. `offset` (4 bytes): The offset for the operation.
6. `numBytes` (4 bytes): The number of bytes to be processed.

### `marshalClientPacketS2`

This function marshals a `ClientPacket` into a byte array with the following format:

1. `requestId` (4 bytes): The ID of the request.
2. `serviceId` (4 bytes): The ID of the service.
3. `filepath length` (4 bytes): The length of the filepath.
4. `filepath` (variable length): The filepath itself.
5. `offset` (4 bytes): The offset for the operation.
6. `input length` (4 bytes): The length of the input data.
7. `input` (variable length): The input data itself.

### `marshalClientPacketS3`

This function marshals a `ClientPacket` into a byte array with the following format:

1. `requestId` (4 bytes): The ID of the request.
2. `serviceId` (4 bytes): The ID of the service.
3. `filepath length` (4 bytes): The length of the filepath.
4. `filepath` (variable length): The filepath itself.
5. `monitorInterval` (4 bytes): The interval for monitoring.
