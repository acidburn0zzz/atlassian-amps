#!/bin/sh

# Add the install size to our plist file
DIRSIZE=`du -ks target/unzip | cut -f 1`
sed -i "s/SIZE/$DIRSIZE/g" target/osx-work/Contents/Info.plist

# Make the pax file
cd target/unzip
pax -w -xcpio -z . >../osx-work/Contents/Archive.pax.gz

exit 0
