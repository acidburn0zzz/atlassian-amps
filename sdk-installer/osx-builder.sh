#!/bin/sh

rm ./target/*.pkg
rm -rf ./target/product-unzip
rm -rf ./target/archive-tmp

#If we have a SNAPSHOT, use a timestamp version
VERSION=$1
TS=`date +%Y%m%d%H%M%S`
VERSION=$(echo "$VERSION" | sed "s/-SNAPSHOT/~$TS/")

sed -i -e "s/VERSION/$VERSION/g" target/osx-work/distribution.xml

echo "using PackageInfo version: $VERSION"

echo "Copying uninstaller..."
cp ./target/osx-work/uninstall.sh ./target/osx-unzip/atlassian-plugin-sdk-$1/

echo "Copying installtype.txt..."
cp ./target/osx-work/installtype.txt ./target/osx-unzip/atlassian-plugin-sdk-$1/

echo "running pkgbuild..."
pkgbuild --identifier com.atlassian.atlassian-plugin-sdk --install-location /usr/share/atlassian-plugin-sdk-$1 --ownership preserve --scripts ./target/osx-work/scripts/ --version "$VERSION" --root ./target/osx-unzip/atlassian-plugin-sdk-$1 ./target/atlassian-sdk.pkg

echo "running productbuild..."
productbuild --sign "3rd Party Mac Developer Installer" --distribution ./target/osx-work/distribution.xml --package-path ./target/ --resources ./target/osx-work/resources ./target/atlassian-plugin-sdk-$1.pkg

exit 0
