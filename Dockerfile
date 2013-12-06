from ubuntu:12.04

run echo "deb http://archive.ubuntu.com/ubuntu precise main universe" > /etc/apt/sources.list
run apt-get -y update
run apt-get -y install wget git supervisor
run wget -O - http://nodejs.org/dist/v0.8.26/node-v0.8.26-linux-x64.tar.gz | tar -C /usr/local/ --strip-components=1 -zxv
expose 3000

add . /opt/dongsa
run cd /opt/dongsa && npm install

workdir /opt/dongsa
cmd ["node", "server.js"]