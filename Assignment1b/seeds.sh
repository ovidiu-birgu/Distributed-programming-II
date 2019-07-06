#!/bin/bash

#seedsArray=( 1220329 1220343 1204310 )
seedsArray=( 100 )

if [[ $# -eq 1 ]] && [[ $1 == "-clean" ]]; then
	rm seed*.txt seed*.xml testout.xml
else
	for seed in "${seedsArray[@]}"
	do
		for i in $(seq 0 2)
		do
			ant -Dseed=$seed -Dtestcase=$i RnsInfo > seed$seed-testcase$i.txt
			ant -Dseed=$seed -Dtestcase=$i -Doutput=seed$seed-testcase$i-serializer.xml RnsInfoSerializer > seed$seed-testcase$i-serializer.txt
			ant -Dseed=$seed -Dtestcase=$i runFuncTest > seed$seed-testcase$i-runFuncTest.txt
		done
	done	
fi





