#!/bin/sh

# Make sure we have a clean /usr/bin
rm -rf target/rpm-work/unzip/usr/bin/
mkdir -p target/rpm-work/unzip/usr/bin/

# Add the symlinks we need from /usr/share to /usr/bin
for f in `find target/rpm-work/unzip/usr/share/atlassian-plugin-sdk-$1/bin/ -name "atlas-*" | xargs -n1 basename`; do
  ln -s /usr/share/atlassian-plugin-sdk-$1/bin/$f target/rpm-work/unzip/usr/bin/$f
done

#If we have an xx-SNAPSHOT we need to change the version to xx_TIMESTAMP
VERSION=$1
TS=`date +%Y%m%d%H%M%S`
VERSION=$(echo "$VERSION" | sed "s/-SNAPSHOT/_$TS/")

# update version in spec
sed -i -e "s/SDKVERSION/$VERSION/g" target/rpm-work/atlassian-plugin-sdk.spec

# update filename version in spec
sed -i -e "s/FILEVERSION/$1/g" target/rpm-work/atlassian-plugin-sdk.spec

# update postinst script
sed -i -e '/POSTINST/{r ./target/deb-work/DEBIAN/postinst' -e 'd}' target/rpm-work/atlassian-plugin-sdk.spec

# update prerm script
sed -i -e '/PRERM/{r ./target/deb-work/DEBIAN/prerm' -e 'd}' target/rpm-work/atlassian-plugin-sdk.spec

# update postrm script
sed -i -e '/POSTRM/{r ./target/deb-work/DEBIAN/postrm' -e 'd}' target/rpm-work/atlassian-plugin-sdk.spec

echo "using rpm version: $VERSION"

# Make the rpm file
#cd ./target/rpm-work
rpmbuild -v --buildroot=${PWD}/target/rpm-work/unzip -bb --target noarch ./target/rpm-work/atlassian-plugin-sdk.spec 2>&1


exit 0
