### INF563 (2015-2016)
### Copyright (C) 2015 by M. Carrière and S. Oudot

# plots
library(rgl)

# iterate over all clouds
for (i in 1:20) {
    print(i)
    
    # retrieve data
    data = read.table(paste('cloud',i,'.txt', sep=""));

    # perform MDS without data normalization
    source("R/MDS.R")
    L <- MDS(data, FALSE)

    M <- data.matrix(L[[1]]) # data after projection
    V <- L[[2]] # new axes (correlations between old and new variables)

    # output 3-dimensional point cloud to file
    write.table(M[,1:3], file=paste('cloud',i,'_3d.txt', sep=""))    

    # 3-d data plot
    open3d()
    plot3d(M[,1], M[,2], M[,3], size=4)
}
