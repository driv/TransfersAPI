# Transfers API

## Execute

Decompressing the `TransfersAPI-1.0-SNAPSHOT.zip` you can find in the bin directory both `TransferAPI` and `TransferAPI.bat`.
The server will be started on port `8080`.

## Demo

There is a small executable demo in `demo.sh` with curl calls to the server.
The demo assumes that there is no data on the server and that the server is running.

## Build

`gradlew build` build the project
`gradlew assemble` generates deliverables
`gradelw run` starts the server. (it's not handling properly shutdown)


