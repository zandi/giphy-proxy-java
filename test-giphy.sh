#!/bin/bash

# direct test (sanity)
#curl "http://api.giphy.com/v1/gifs/search?q=test&api_key=fN39vpx7tywdcEf3OqnhTCzxMO5wNuo2&limit=2"

# note: curl gives us the following tunnel proxy command doing this:
#  CONNECT api.giphy.com:443 HTTP/1.1
#  Host: api.giphy.com:443
#  User-Agent: curl/7.52.1
#  Proxy-Connection: Keep-Alive
# -x: proxy to use
# -p: use tunneling
# default port for proxy: 1080
curl -x https://localhost:1080 --proxy-cacert proxycert.crt --proxy-insecure -p "https://api.giphy.com/v1/gifs/search?q=test&api_key=fN39vpx7tywdcEf3OqnhTCzxMO5wNuo2&limit=2"

echo
