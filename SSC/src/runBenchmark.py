#!/usr/bin/env python
import sys
import os
import time
import subprocess
import Gnuplot, Gnuplot.funcutils
import re

config = 'config'
resultDir = 'results/'
paramErr = '>> Each set must contain exactly one variable parameter (see usage for help)'

yAxisLabel = 'time in ms'
xAxisLabel = ['Keys Distribution Strategy', 'Number of Keys', 'Key length in bytes',
              'Number of Producers', 'Number of Write Cycles', 'Number of Consumers',
              'Number of Read Cycles']

numSteps = 10

def readSets():
    sets = []
    with open(config, 'r') as f:
        for line in f:
            if(line[0].isdigit()):
                if (line.count('-') == 1):
                    sets.append(line.strip())
                else:
                    print paramErr
                    sys.exit()
    f.close()
    return sets

def getParamLines(set):
    lines = []
    args = set.split()
    count = 0
    for arg in args:
        if "-" in arg:
            # assign position of variable globally for later use
            global varPosition
            varPosition = count
        count = count + 1
    varRange = args[varPosition].split('-')
    isFloat = (re.match("^\d+?\.\d+?$", varRange[0]) is not None) 
    if  isFloat:
        lower= float(varRange[0])
        upper = float(varRange[1])
        step = (upper - lower)/numSteps
        x = lower
        while (x < upper+step):
            tempLine = args[:]
            tempLine[varPosition] = str(x)
            print(' '.join(tempLine))
            x += step
            #lines.append(' '.join(tempLine))
    else:
        for x in range(int(varRange[0]), int(varRange[1]) + 1):
            tempLine = args[:]
            tempLine[varPosition] = str(x)
            lines.append(' '.join(tempLine))
    return lines

def getAverage(output):
    sum = long(0)
    count = 0
    for line in output.split('\n'):
        if (len(line) > 0):
            sum += (long(line))
            count += 1
    return sum / count

def writeDataFile(fileName, valueMap):
    f = open(fileName, 'a')
    for k,v in valueMap.iteritems():
        f.write(str(k)+" "+str(v)+"\n")
    f.close()

def createPlot(valueMap, set):
    if (not os.path.exists(resultDir)):
        os.mkdir(resultDir)
    dataFile = set.replace(' ','_')+".txt"
    if (os.path.exists(resultDir+dataFile)):
        os.remove(resultDir+dataFile)
    writeDataFile(resultDir+dataFile, valueMap)
    plot = Gnuplot.Gnuplot()
    plot.title(set)
    plot('set term pdf font "Helvetica, 10"')
    plot('set style line 1 lt 1 lc rgb "#FF0000" lw 7 # red')
    plot('set output "'+resultDir+set.replace(' ','_')+'.pdf";')
    plot('set ylabel "'+yAxisLabel+'";')
    plot('set xlabel "'+xAxisLabel[varPosition]+'";')
    #plot('set grid x,y;')
    plot('set key top left;')
    plot('set yrange [:]')
    plot('set xrange [:]')
    plot('plot "'+resultDir+dataFile+'" u ($1):($2*0.000001) smooth unique title "'+xAxisLabel[varPosition]+'";')
    
    
    
sets = readSets()
for set in sets:
    avgs = dict()
    for paramLine in getParamLines(set):
        print 'Running with parameters ' + paramLine
        sp = subprocess.Popen('java Mapping ' + paramLine, shell=True, stdout=subprocess.PIPE)
        avg = getAverage(sp.communicate()[0])
        avgs[paramLine.split()[varPosition]] = avg
    createPlot(avgs, set)
        # os.system("java Mapping "+paramLine+" >> "+paramLine.replace(' ','_')+".txt")
