#!/bin/sh

# Save current IFS
SAVEIFS=$IFS

# Change IFS to new line. 
IFS=$'\n'

# Get list of contributors
myarr=($(git log --format='%aN' | sort -u))

# Restore IFS
IFS=$SAVEIFS

# Loop trough all of the contributors
for i in "${myarr[@]}" 
do
	# Print contributor name
	echo "\n$i"
	# Print contributor stats
    git log --shortstat --author "$i" | grep "commit" | awk '{commits+=1} END {print "Commits:", commits}'
    git log --shortstat --author "$i" | grep "files changed" | awk '{files+=$1; inserted+=$4; deleted+=$6} END {print "Files changed:", files, "\nLines inserted:", inserted, "\nLines deleted:", deleted}'
done
echo "\n"
