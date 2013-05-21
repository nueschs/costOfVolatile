#!/usr/bin/env python
import sys
import os
import time
import subprocess
import Gnuplot, Gnuplot.funcutils
import re
import collections

config = 'config'
resultDir = 'results/'
completeDataFileBase = resultDir+'data/confData'
paramErr = '>> Each set must contain exactly one variable parameter (see usage for help)'

yAxisLabel = 'time in ms'
xAxisLabel = ['Number of Keys','Key length in bytes','Number of Threads',
              'Number of read / write Cycles', 'Read / Write ratio', 'Key set overlapping',
              'Number of benchmark runs']

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
        while (x <= upper):
            tempLine = args[:]
            tempLine[varPosition] = str(x)
            print(' '.join(tempLine))
            x += step
            lines.append(' '.join(tempLine))
    else:
		step = (int(varRange[1])-int(varRange[0]))/numSteps
		x = int(varRange[0])
		while (x < int(varRange[1])):
			tempLine = args[:]
			tempLine[varPosition] = str(x)
			lines.append(' '.join(tempLine))
			x = x+step
    return lines

def getAverage(output):
    sum = long(0)
    count = 0
    for line in output.split('\n'):
        if (len(line) > 0):
            sum += (long(line))
            count += 1
    return sum / count

def getMin(output):
    min = long(sys.maxint)
    for line in output.split('\n'):
        if (len(line) > 0):
            val = long(line)
            if (val < min):
                min = val
    return min

def getMax(output):
    max = long(-1)
    for line in output.split('\n'):
        if (len(line) > 0):
            val = long(line)
            if (val > max):
                max = val
    return max

def writeDataFile(fileName, valueMap):
    f = open(fileName, 'a')
    orderedValueMap = collections.OrderedDict(sorted(valueMap.items()))
    for k,v in orderedValueMap.iteritems():
        line = ""
        for val in v:
            line += str(val)+" "
        f.write(str(k)+" "+line+"\n")
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
    plot('set style line 1 lt 1 lc rgb "#FF0000" lw 3 # red')
    plot('set output "'+resultDir+set.replace(' ','_')+'.pdf";')
    plot('set ylabel "'+yAxisLabel+'";')
    plot('set xlabel "'+xAxisLabel[varPosition]+'";')
    #plot('set grid x,y;')
    plot('set key top left;')
    plot('set yrange [:]')
    plot('set xrange [:]')
    plot('plot "'+resultDir+dataFile+'" using ($1):($2*0.000001):($3*0.000001):($4*0.000001) with yerrorlines title "'+xAxisLabel[varPosition]+'";')
    
    
    
sets = readSets()
for set in sets:
    avgs = dict()
    count = 0
    if (os.path.exists(completeDataFileBase+set.replace(' ','_'))):
            os.remove(completeDataFileBase+set.replace(' ','_'))
    for paramLine in getParamLines(set):
        print 'Running with parameters ' + paramLine
        sp = subprocess.Popen('java -server Mapping ' + paramLine, shell=True, stdout=subprocess.PIPE)
        #sp = subprocess.Popen('java -Xms2m -Xmx2m -server Mapping ' + paramLine, shell=True, stdout=subprocess.PIPE)
        output = sp.communicate()[0]
        avg = getAverage(output)
        min = getMin(output)
        max = getMax(output)
        f = open(resultDir+"data/temp"+str(count), 'a')
        f.write(output)
        f.close()
        count = count + 1
        avgs[paramLine.split()[varPosition]] = [avg,min,max]
    createPlot(avgs, set)
        # os.system("java Mapping "+paramLine+" >> "+paramLine.replace(' ','_')+".txt")
