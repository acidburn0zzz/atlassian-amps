#!/bin/sh

# Make sure we have a clean /usr/bin
rm -rf target/deb-work/usr/bin/
mkdir -p target/deb-work/usr/bin/

# Add the symlinks we need from /usr/share to /usr/bin
for f in `find target/deb-work/usr/share/atlassian-plugin-sdk-3.12-SNAPSHOT/bin/ -name "atlas-*" | xargs -n1 basename`; do
  ln -s /usr/share/atlassian-plugin-sdk-3.12-SNAPSHOT/bin/$f target/deb-work/usr/bin/$f
done

# Add the install size to our conbtrol file
DIRSIZE=`du -ks target/unzip | cut -f 1`
sed -i "s/SIZE/$DIRSIZE/g" target/deb-work/DEBIAN/control

# Make the deb file
dpkg --build target/deb-work target

exit 0
