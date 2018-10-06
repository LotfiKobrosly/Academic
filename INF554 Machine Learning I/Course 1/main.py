from numpy import *
from matplotlib.pyplot import *
from scipy.linalg import inv
from sklearn.decomposition import PCA

# Load the data

data = loadtxt('data/data_train.csv', delimiter=',')

# Prepare the data

X = data[:,0:-1]
y = data[:,-1]

# Inspect the data

#figure()
#hist(X[:,2], 10)

# <TASK 1>

#figure()
#plot(X[:,0],y, 'o')
#xlabel('x2')
#ylabel('x3')

# <TASK 2>

#show()

# Standardization

M = mean(X, axis = 0)
D = std(X, axis = 0)

X = (X - M)/D


# Feature creation

from tools import poly_exp, MSE

# Lists of MSEs

L_test = []
L_train = []

for i in range(1,10):

	pca = PCA(n_components = 2)

	principalcomponents = pca.fit_transform(X)

	Z = poly_exp(X,i)
	Z = column_stack([ones(len(Z)), Z])

	# Building a model

	# <TASK 3>
	Z_t = transpose(Z)
	w = dot( dot( inv( dot(Z_t,Z) ), Z_t ), y )

	# Evaluation 


	# <TASK 4>

	data_test = loadtxt('data/data_test.csv', delimiter = ',')

	X_test = data_test[:,0:-1]
	y_test = data_test[:,-1]

	X_test = (X_test - M)/D

	pca_test = PCA(n_components = 2)

	principalcomponents = pca_test.fit_transform(X_test)

	Z_test = poly_exp(X_test,i)
	Z_test = column_stack([ones(len(Z_test)),Z_test])

	y_pred = dot(Z_test,w)

	y_mean = mean(y, axis = 0)

	# <TASK 5>

	mse = MSE(y_test,y_pred)
	mse_train = MSE(y_pred,y_mean)

	L_test.append(mse)
	L_train.append(mse_train)

abcissae = arange(1,10)

figure()
plot(abcissae,L_test, color = "red", label = "MSE on test data")
plot(abcissae, L_train, color = "blue", label = "MSE on train data")
legend()
show()

