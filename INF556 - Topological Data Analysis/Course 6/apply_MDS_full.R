### INF563 (2015-2016)
### Copyright (C) 2015 by M. Carrière and S. Oudot

# plots
library(rgl)

    # retrieve data
    data = read.table('cloud_full.txt');

    # perform MDS without data normalization
    source("R/MDS.R")
    L <- MDS(data, FALSE)

    M <- data.matrix(L[[1]]) # data after projection
    V <- L[[2]] # new axes (correlations between old and new variables)

    # output 3-dimensional point cloud to file
    write.table(M[,1:3], file='cloud_full_3d.txt')    

    # 3-d data plot
    open3d()
    plot3d(M[,1], M[,2], M[,3], size=6, col=rainbow(20)[cbind(rep(1,72),rep(2,72),rep(3,72),rep(4,72),rep(5,72),rep(6,72),rep(7,72),rep(8,72),rep(9,72),rep(10,72),rep(11,72),rep(12,72),rep(13,72),rep(14,72),rep(15,72),rep(16,72),rep(17,72),rep(18,72),rep(19,72),rep(20,72))])

