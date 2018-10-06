from numpy import *

def poly_exp(X, degree):
    N,D = X.shape
    for d in range(2,degree+1):
        X = column_stack([X,X[:,0:D]**d])
    return X

def MSE(yt,yp):
    N = len(yt)
    R = zeros(N)
    #for i in range(N):
    #	R[i] = (yt[i] - yp[i])**2
    R = yt - yp
    R = dot ( transpose(R) , R )
    R = R/N

    return R
