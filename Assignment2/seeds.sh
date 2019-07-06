#!/bin/bash

#seedsArray=( 1220329 1220343 1204310 100 1152510 2167182)
seedsArray=( 1152510 )

if [[ $# -eq 1 ]] && [[ $1 == "-clean" ]]; then
	rm seed*.txt testout.xml
else
	# how many iterations
	for i in $(seq 0 1)
	do
		# for all seeds
		for seed in "${seedsArray[@]}"
		do
			# for all test cases
			for j in $(seq 0 2)
			do
				ant -Dseed=$seed -Dtestcase=$j runFuncTest > seed$seed-testcase$j-$i-runFuncTest.txt
			done
		done
	done
fi





