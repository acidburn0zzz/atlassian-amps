#!/bin/sh

SDKVERSION=$1
RPMVERSION=$2

#make sure we use GNU sed (gsed from macports) on OSX

OS=`uname`

echo "OS = $OS"

if [ "${OS}" == "Darwin" ]; then
    SED=gsed
else
	SED=sed
fi

# Make sure we have a clean /usr/bin
rm -rf target/rpm-work/unzip/usr/bin/
mkdir -p target/rpm-work/unzip/usr/bin/

# Add the symlinks we need from /usr/share to /usr/bin
for f in `find target/rpm-work/unzip/usr/share/atlassian-plugin-sdk-$SDKVERSION/bin/ -name "atlas-*" | xargs -n1 basename`; do
  ln -s /usr/share/atlassian-plugin-sdk-$SDKVERSION/bin/$f target/rpm-work/unzip/usr/bin/$f
done

# update version in spec
$SED -i -e "s/RPMVERSION/$RPMVERSION/g" target/rpm-work/atlassian-plugin-sdk.spec

# update postinst script
$SED -i -e '/POSTINST/{r ./target/deb-work/DEBIAN/postinst' -e 'd}' target/rpm-work/atlassian-plugin-sdk.spec

# update prerm script
$SED -i -e '/PRERM/{r ./target/deb-work/DEBIAN/prerm' -e 'd}' target/rpm-work/atlassian-plugin-sdk.spec

# update postrm script
$SED -i -e '/POSTRM/{r ./target/deb-work/DEBIAN/postrm' -e 'd}' target/rpm-work/atlassian-plugin-sdk.spec

echo "using rpm version: $RPMVERSION"

# Make the rpm file
#cd ./target/rpm-work
sudo rpmbuild -v --buildroot=${PWD}/target/rpm-work/unzip -bb --target=noarch --buildos=Linux ./target/rpm-work/atlassian-plugin-sdk.spec 2>&1


exit 0
