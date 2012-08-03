Buildroot: ${project.build.directory}/rpm-work/unzip
Name: atlassian-plugin-sdk
Version: SDKVERSION
Release: 1
Summary: Development kit to build Atlassian plugins
License: Apache-2.0
Group: misc

%define _rpmdir ${project.build.directory}
%define _rpmfilename %%{NAME}-FILEVERSION.%%{ARCH}.rpm
%define _unpackaged_files_terminate_build 0

%post
POSTINST


%preun
PRERM


%postun
POSTRM


%description


%clean


%files
"/"