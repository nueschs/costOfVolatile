#!/usr/bin/env bash
# TODO clean help messages
FILE=config
MULTI_VAR_MSG=">> Each set must contain exactly one variable parameter (see usage for help)"

get_param_line(){
	
}

declare -a sets=();
while read line
do
	if [[ $line == [0-9]* ]]; 
	then
		if [[ `echo $line | tr -cd "-" | wc -c;` -ne 1 ]]
		then
			echo $MULTI_VAR_MSG
			exit
		else
			sets=("${sets[@]}" $line)			
		fi
	fi
done < "$FILE"

for set in "${sets[@]}"; do
	param_line=$(get_param_line )
done;
