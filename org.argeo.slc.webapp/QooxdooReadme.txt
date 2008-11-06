Using the build scripts
=======================

1/ First make sur the following lines point to the right location (your qooxdoo SDK) : 
File generate.py Line 28
File config.json Line 7

2/ The qooxdoo framework content (not the whole sdk, just the 
framework part, ie the classes, resources, etc) must be located at 
"src/main/webapp/qooxdoo" for these target to work.

3/ Then Use the following targets : slc-source  / slc-build 

generate.py slc-source : will compile the source file
generate.py slc-build : will compile the build file