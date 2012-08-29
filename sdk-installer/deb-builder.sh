#!/bin/sh

SDKVERSION=$1
DEBVERSION=$2

#make sure we use GNU sed (gsed from macports) on OSX

OS=`uname`

echo "OS = $OS"

if [ "${OS}" == "Darwin" ]; then
    SED=gsed
else
	SED=sed
fi

# Make sure we have a clean /usr/bin
rm -rf target/deb-work/usr/bin/
mkdir -p target/deb-work/usr/bin/

# Add the symlinks we need from /usr/share to /usr/bin
for f in `find target/deb-work/usr/share/atlassian-plugin-sdk-$SDKVERSION/bin/ -name "atlas-*" | xargs -n1 basename`; do
  ln -s /usr/share/atlassian-plugin-sdk-$SDKVERSION/bin/$f target/deb-work/usr/bin/$f
done

# Add the install size to our control file
DIRSIZE=`du -ks target/deb-work/usr/share/atlassian-plugin-sdk-$SDKVERSION | cut -f 1`
$SED -i -e "s/SIZE/$DIRSIZE/g" target/deb-work/DEBIAN/control

# update version in control
$SED -i -e "s/DEBVERSION/$DEBVERSION/g" target/deb-work/DEBIAN/control

echo "using deb version: $DEBVERSION"

# Make the deb file
dpkg --build target/deb-work target/atlassian-plugin-sdk-$DEBVERSION.deb

exit 0
