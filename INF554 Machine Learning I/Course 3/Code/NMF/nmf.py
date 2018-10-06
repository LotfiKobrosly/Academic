import numpy as np
from numpy.linalg import norm,svd
from time import time
from sys import stdout


def nmf_factor(V, r, iterations=100):
    n, m = V.shape
    W = np.ones((n, r))
    H = np.ones((r, m))
    d_iter = np.ones(100)
    return W, H, d_iter
