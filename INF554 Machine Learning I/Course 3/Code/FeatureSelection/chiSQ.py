# Feature selection with the Chi^2 measure

from numpy import *

def chiSQ(x, y):
    '''
        x: features (data)
        y: output (classes)
    '''
    cl = unique(y) # unique number of classes
    rows = x.shape[0]
    dim = x.shape[1]
    chisq = zeros(dim) # initialize array (vector) for the chi^2 values
    
    # ====================== YOUR CODE HERE ======================
    # Instructions: calculate the importance for ecah feature
    
    for j in range(dim):
        rw = unique(x[:,j])
        O = zeros((rw.shape[0],cl.shape[0]))
        E = zeros((rw.shape[0],cl.shape[0]))
        for c in range(cl.shape[0]):
            for r in range(rw.shape[0]):
                O[r,c] = len(where(logical_and((x[:,j] == x[r,j]),(y == cl[c]))))
                E[r,c] = (1/rows)*len(where(x[:,j] == x[r,j]))*len(where(y == cl[c]))

    for i in range(dim):
        for j in range(cl.shape[0]):
            chisq[i] += ((O[i,j] - E[i,j])**2)/E[i,j]

    return chisq
