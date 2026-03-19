#!/usr/bin/env sh
set -eu

PORT_TO_USE="${PORT:-8080}"

# Update Tomcat connector port to match Railway's $PORT
SERVER_XML="/usr/local/tomcat/conf/server.xml"

if [ -f "$SERVER_XML" ]; then
  # Replace the first occurrence of port="8080" in the Connector line
  # (Tomcat ships with 8080 as default)
  sed -i "0,/port=\"8080\"/s//port=\"${PORT_TO_USE}\"/" "$SERVER_XML"
fi

exec catalina.sh run

