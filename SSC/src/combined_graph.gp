#set style line 1 lt 1 lc rgb "#FF0000" lw 3 # red
set terminal pdf size 320,240 font "Helvetica, 400"
set output "combined_graph.pdf";
set ylabel 'time in ms';
set xlabel 'Overlapping percentage';
set key top left;
set yrange [:400];
set xrange [:];
plot '100_3_16_500000_0_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '0% Overlap',\
	'100_3_16_500000_10_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '10% Overlap',\
	'100_3_16_500000_20_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '20% Overlap',\
	'100_3_16_500000_30_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '30% Overlap',\
	'100_3_16_500000_40_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '40% Overlap',\
	'100_3_16_500000_50_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '50% Overlap',\
	'100_3_16_500000_60_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '60% Overlap',\
	'100_3_16_500000_70_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '70% Overlap',\
	'100_3_16_500000_80_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '80% Overlap',\
	'100_3_16_500000_90_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '90% Overlap',\
	'100_3_16_500000_100_0.0-0.9_10.txt' using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title '100% Overlap'
