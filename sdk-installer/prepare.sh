#!/bin/sh

# 2.1.0
mvn --version

# 1.6
javac -version

# 5.4.14
rpm --version

# 5.4.14
rpmbuild --version

# 1.17.6
dpkg --version

# 4.2.2
gsed --version

# installbuilder ??

# Slightly awkward because a) --version is a command for the build itself, and b) appears to be OS X specific
pkgbuild

# As per pkgbuild
productbuild



