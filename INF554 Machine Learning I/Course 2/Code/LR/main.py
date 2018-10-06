from numpy import *
from math import *
import matplotlib.pyplot as plt
import scipy.optimize as op
from decimal import *

def sigmoid(z):
    # Computes the sigmoid of z.
    z_neg = Decimal.from_float(-1.0) * z
    tmp = Decimal(1) + z_neg.exp()
    return (Decimal(1)/tmp)
    # =============================================================
             

def cost(theta, X, y): 
    # Computes the cost using theta as the parameters for logistic regression. 
    
    #Initializing result
    res = 0
    Z = dot(X,theta)
    for i in range(X.shape[0]):
        z = Decimal.from_float(Z[i])
        #print(z)
        a = sigmoid(z)
        #print(a)
        ln1 = float(a.ln())
        ln2 = float( (Decimal(1) - a).ln() )
        res = res + y[i]*ln1 + (1-y[i])*ln2


    return((-1)*res/X.shape[0])
    # =============================================================


def compute_grad(theta, X, y):
    # Computes the gradient of the cost with respect to
    # the parameters.
    
    grad = zeros(size(theta)) # initialize gradient
    # ====================== YOUR CODE HERE ======================

    #Computing gradient
    Z = dot(X,theta)
    for j in range(theta.shape[0]):
        for i in range(X.shape[0]):
            z = Decimal.from_float(Z[i])
            a = float(sigmoid(z))
            grad[j] += (a - y[i])*X[i,j]
        grad[j] = grad[j]/X.shape[0]

    # =============================================================
    
    return grad


def predict(theta, X):
    # Predict whether each label is 0 or 1 using learned logistic 
    # regression parameters theta. The threshold is set at 0.5
    
    # ====================== YOUR CODE HERE ======================
    
    #Initializing results
    y_pred = zeros(X.shape[0])

    #Computing p(c=1|x)
    print(theta)
    Z = dot(X,theta)
    for i in range(X.shape[0]):
        z = Decimal.from_float(Z[i])
        #z1 = Decimal(power(z,Decimal(y[i])))
        #z2 = Decimal( power(Decimal(1) - z, Decimal(1-y[i])))
        #a1 = float(sigmoid(z1))
        #a2 = float(sigmoid(z2))
        a = float(sigmoid(z))
        if (a > 0.5):
            y_pred[i] = 1

    return y_pred
    # =============================================================
    


#======================================================================
# Load the dataset
# The first two columns contains the exam scores and the third column
# contains the label.
data = loadtxt('./data/data.txt', delimiter=',')
 
X = data[:, 0:2]
y = data[:, 2]

# Plot data 
pos = where(y == 1) # instances of class 1
neg = where(y == 0) # instances of class 0
plt.scatter(X[pos, 0], X[pos, 1], marker='o', c='b')
plt.scatter(X[neg, 0], X[neg, 1], marker='x', c='r')
plt.xlabel('Exam 1 score')
plt.ylabel('Exam 2 score')
plt.legend(['Admitted', 'Not Admitted'])
plt.show()


#Add intercept term to X
X_new = ones((X.shape[0], 3))
X_new[:, 1:3] = X
X = X_new

# Initialize fitting parameters
initial_theta = zeros((3,1))

# Run minimize() to obtain the optimal theta
Result = op.minimize(fun = cost, x0 = initial_theta, args = (X, y), method = 'TNC',jac = compute_grad);
theta = Result.x;

# Plot the decision boundary
plot_x = array([min(X[:, 1]) - 2, max(X[:, 2]) + 2])
plot_y = (- 1.0 / theta[2]) * (theta[1] * plot_x + theta[0])
plt.plot(plot_x, plot_y)
plt.scatter(X[pos, 1], X[pos, 2], marker='o', c='b')
plt.scatter(X[neg, 1], X[neg, 2], marker='x', c='r')
plt.xlabel('Exam 1 score')
plt.ylabel('Exam 2 score')
plt.legend(['Decision Boundary', 'Admitted', 'Not Admitted'])
plt.show()

# Compute accuracy on the training set
p = predict(array(theta), X)
# Evaluation
accuracy = mean(p == y)
print("\nAccuracy: %4.3f" % accuracy)
