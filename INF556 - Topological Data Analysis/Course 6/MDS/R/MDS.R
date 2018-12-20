### INF563 (2015-2016)
### Copyright (C) 2015 by M. Carrière and S. Oudot

MDS <- function(cloud, norm=FALSE){

# number of data points (= number of lines)
np <- length(cloud[[1]])

# convert data frame to numeric matrix
m <- data.matrix(cloud)

# center (and normalize) data
m <- scale(m, scale=norm)

# compute Gram matrix
gram <- (m %*% t(m))

# diagonalize Gram matrix
eig <- eigen(gram)

# retrieve eigenvectors and eigenvalues
vec <- eig$vectors
vars <- eig$values

# compute new coordinates
D <- sqrt(diag(vars))
reduced <- vec %*% D

# output 
L <- list("points" <- reduced, "variables" <- vec, "spectrum" <- vars)

return(L)
}
