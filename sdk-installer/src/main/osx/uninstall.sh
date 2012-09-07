#!/bin/sh

if [ `whoami` != root ]; then
    echo "Please run this script as root or using sudo"
    exit
fi

RemoveSDK () {
    echo "removing files..."
    # remove the symlinks from /usr/local/bin
    for f in `find /usr/local/bin/ -name "atlas-*"`; do
      rm $f
    done

    #remove the sdk from /usr/share
    rm -rf /usr/share/atlassian-plugin-sdk-${project.version}

    echo "Atlassian Plugin SDK has been uninstalled"
}

while true; do
    read -p "This will completely remove Atlassian Plugin SDK v${project.version}! Do you wish to continue? (Y/N) " yn
    case $yn in
        [Yy]* ) RemoveSDK; break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

