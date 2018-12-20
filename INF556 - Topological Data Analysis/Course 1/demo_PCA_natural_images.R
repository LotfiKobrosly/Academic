### INF563 (2015-2016)
### Copyright (C) 2015 by M. Carrière and S. Oudot

# retrieve data
data=read.table('data/natural_images.dat')

# perform PCA without data normalization
source("code/PCA.R")
L <- PCA(data, FALSE)

M <- data.matrix(L[[1]]) # data after projection
V <- L[[2]] # new axes (correlations between old and new variables)
Var <- L[[3]] # cumulative variance
S <- L[[4]] # spectrum


# plots
library(rgl)

# spectrum plot
dev.new()
plot(S)

# cumulative variance plot
dev.new()
plot(Var)

# data plot using first 2 intrinsic variables
dev.new()
plot(M[1,], M[2,], col="blue", pch = 19, lty = "solid")

# data plot using first 3 intrinsic variables
open3d()
plot3d(M[1,], M[2,], M[3,], size=4)

# correlation circles (intrinsic variables w.r.t. input variables)
dev.new()
plot(t(V)[1,], t(V)[2,], col="blue", pch = 19, lty = "solid", asp = 1, xlim = c(-1, 1), ylim = c(-1, 1))
text(t(V)[1,], t(V)[2,], labels=seq(1:8), pos=3)
library("plotrix")
draw.circle(0, 0, 1, nv=100, border=NULL, col=NA, lty=1, lwd=1)
dev.new()
plot(t(V)[3,], t(V)[4,], col="blue", pch = 19, lty = "solid", asp = 1, xlim = c(-1, 1), ylim = c(-1, 1))
text(t(V)[3,], t(V)[4,], labels=seq(1:8), pos=3)
draw.circle(0, 0, 1, nv=100, border=NULL, col=NA, lty=1, lwd=1)
dev.new()
plot(t(V)[5,], t(V)[6,], col="blue", pch = 19, lty = "solid", asp = 1, xlim = c(-1, 1), ylim = c(-1, 1))
text(t(V)[5,], t(V)[6,], labels=seq(1:8), pos=3)
draw.circle(0, 0, 1, nv=100, border=NULL, col=NA, lty=1, lwd=1)
dev.new()
plot(t(V)[7,], t(V)[8,], col="blue", pch = 19, lty = "solid", asp = 1, xlim = c(-1, 1), ylim = c(-1, 1))
text(t(V)[7,], t(V)[8,], labels=seq(1:8), pos=3)
draw.circle(0, 0, 1, nv=100, border=NULL, col=NA, lty=1, lwd=1)
