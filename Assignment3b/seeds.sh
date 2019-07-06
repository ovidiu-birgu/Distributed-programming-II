#!/bin/bash

# ok seeds
# 100 1085046 1220343 1220329 2167182 1083622 1084842 1204310 1152510 7777
# error seeds
#    

# current seeds
seedsArray=( 11223 )

if [[ $# -eq 1 ]] && [[ $1 == "-clean" ]]; then
	rm seed*.txt testout.xml
else
	# for all seeds
	for seed in "${seedsArray[@]}"
	do
		ant -Dseed=$seed run-tests > seed$seed.txt
	done
fi





