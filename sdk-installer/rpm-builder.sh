#!/bin/sh

cd target/
sudo alien --to-rpm --scripts -k -v ./atlassian-plugin-sdk-$1.deb

exit 0
