#!/bin/bash

# send something that isn't even SSL, check server logs
nc localhost 1080 <<EOF
this isn't even SSL!
EOF

# send a non-HTTP CONNECT command
curl -k https://localhost:1080
