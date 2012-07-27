#!/bin/sh

cd target/
alien --to-rpm --scripts -v -k ./atlassian-plugin-sdk_$1_all.deb

exit 0
