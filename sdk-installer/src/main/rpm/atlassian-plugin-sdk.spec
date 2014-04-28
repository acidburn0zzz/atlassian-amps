AutoReqProv: no
Buildroot: ${project.build.directory}/rpm-work/unzip
Name: atlassian-plugin-sdk
Vendor: Atlassian
Version: RPMVERSION
BuildArch: noarch
Release: 1
Summary: Development kit to build Atlassian plugins
License: Apache-2.0
Group: misc

%define _target_os linux
%define _rpmdir ${project.build.directory}
%define _rpmfilename %%{NAME}-RPMVERSION.noarch.rpm
%define _unpackaged_files_terminate_build 0
%define _tmppath ${project.build.directory}/tmp


%post
POSTINST


%preun
PRERM


%postun
POSTRM


%description


%clean
echo "clean"

%files
"/usr/bin/*"
"/usr/share/atlassian*"