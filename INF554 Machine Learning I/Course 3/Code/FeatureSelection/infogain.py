# Feature selection with the Information Gain measure

from numpy import *
from math import log


def infogain(x, y):
    '''
        x: features (data)
        y: output (classes)
    '''
    info_gains = zeros(x.shape[1]) # features of x
    
    # calculate entropy of the data *hy* with regards to class y
    cl = unique(y)
    hy = 0
    for i in range(len(cl)):
        c = cl[i]
        py = float(sum(y==c))/len(y) # probability of the class c in the data
        hy = hy+py*log(py,2)
    
    hy = -hy

    # ====================== YOUR CODE HERE ================================
    # Instructions: calculate the information gain for each column (feature)
    for l in range(x.shape[1]):

        rw = unique(x)
        s = 0
        for j in range(len(rw)):
            r = rw[j]
            k = len(where(x==r))
            for i in range(len(cl)):
                c = cl[i]
                py = float(sum((y==c) and (x==r)))/k
                s = s + py*log(py,2)
                s = s*k/len(y)
        info_gains[l] = hy - s

    return info_gains
    