### INF563 (2015-2016)
### Copyright (C) 2015 by M. Carrière and S. Oudot

# retrieve data
data <- read.table('data/swiss_roll.dat')
sol <- read.table('data/swiss_roll_sol.dat')

# perform PCA without data normalization
source("code/PCA.R")
L <- PCA(data, FALSE)

M <- data.matrix(L[[1]]) # data after projection
V <- L[[2]] # new axes (correlations between old and new variables)
Var <- L[[3]] # cumulative variance
S <- L[[4]] # spectrum


# plots
library(rgl)

# input data plot
open3d()
plot3d(data[,1], data[,2], data[,3], col=sol[,1], size=10)

# spectrum plot
dev.new()
plot(S)

# cumulative variance plot
#dev.new()
#plot(Var)

# data plot using first 2 intrinsic variables and color from solution
open3d()
plot3d(M[2,], 0, M[1,], col=sol[,1], size=10)

# input data plot colored with the first 2 intrinsic coordinates
# -> the first intrinsic variable is along the z-axis
# -> the inversions of the color order + the large bands around the silhouette show that the second intrinsic variable is inside the xy-plane
open3d()
plot3d(data[,1], data[,2], data[,3], col=t(M)[,1]-min(M[1,])+1, size=10)
open3d()
plot3d(data[,1], data[,2], data[,3], col=t(M)[,2]-min(M[2,])+1, size=10)
