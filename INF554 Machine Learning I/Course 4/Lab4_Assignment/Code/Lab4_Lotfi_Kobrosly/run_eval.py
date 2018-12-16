import numpy as np
from my_net import Network
from sklearn.multiclass import OneVsRestClassifier
from sklearn.multioutput import ClassifierChain
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.svm import SVC
from time import *
import matplotlib.pyplot as plt 

###############################################################
# This is an example script, you may modify it as you wish
###############################################################

# Load and parse the data (N instances, D features, L=3 labels)
XY = np.genfromtxt('data/scene.csv', skip_header=1, delimiter=",")
N,DL = XY.shape
L = 6
D = DL - L
Y = XY[:,0:L].astype(int)
X = XY[:,L:D+L]

# Split into train/test sets
n = int(N*6/10)
X_train = X[0:n]
Y_train = Y[0:n]
X_test = X[n:]
Y_test = Y[n:]


plt.figure()

# Values to test on

learning_rates = [0.011,0.01,0.009]
sizes = [230,240,250,260]
n_layers = [1,2,3]
epochs_list = [10,50,100,200,500]


all_hamming_losses = []
all_duration = []
overfitting = []

"""

# Influence of the learning rate

for j in range(len(learning_rates)):
	t0 = clock()

	# Test our classifier 
	h = Network(learning_rate = learning_rates[j])

	i = 0
	epochs = 200
	durations = []
	iterations = []
	hamming_losses = []

	while (clock() - t0) < 120:
	    h.fit(X_train,Y_train,n_epochs=epochs)
	    i = i + epochs
	    print("Current epoch :",i)
	    print("Time elapsed :",clock() - t0)
	    iterations.append(i)
	    hamming_losses.append(np.mean(h.predict(X_test) != Y_test))
	    print("Hamming loss: ", hamming_losses[-1])
	    durations.append(clock() - t0)

	

	print("Trained %d epochs in %d seconds." % (i,int(clock() - t0)))
	Y_pred = h.predict(X_test)
	print(Y_test)
	print(Y_pred)
	loss = np.mean(Y_pred != Y_test)
	print("Hamming loss: ", loss)
	print("Overfitting hamming :",np.mean(h.predict(X_train) != Y_train))
	durations.append(clock() - t0)
	for o in range(len(durations) - 1):
		durations[o] = durations[o+1] - durations[o]
	durations.remove(durations[-1])
	legend = "alpha="+str(learning_rates[j])
	r, g, b = 1, 0, 0
	if (j%3 == 1):
		r,g,b = 0,1,0
	if (j%3 == 2):
		r,g,b = 0,0,1
	plt.plot(iterations, hamming_losses, color = (r,g,b), label=legend )
	all_hamming_losses.append(hamming_losses)
	all_duration.append([learning_rates[j], durations[:]])

plt.xlabel("Iteration")
plt.ylabel("Hamming Loss")
plt.legend()
plt.title("Hammering Loss for different alpha values")
plt.savefig("Influence of learning rate.png")
plt.show()


all_hamming_losses.insert(0,iterations)
print(np.array(all_hamming_losses))
for j in range(len(all_duration)):
	all_duration[j] = [all_duration[j][0], np.mean(np.array(all_duration[j][1]))]
"""

#Influence of the layers size / number of layers

"""

# Test our classifier 

for j in range(len(sizes)):
	t0 = clock()
	h = Network(hidden_layer_size = sizes[j])

	i = 0
	epochs = 200
	durations = []
	iterations = []
	hamming_losses = []

	while (clock() - t0) < 120:
	    h.fit(X_train,Y_train,n_epochs=epochs)
	    i = i + epochs
	    print("Current epoch :",i)
	    print("Time elapsed :",clock() - t0)
	    iterations.append(i)
	    hamming_losses.append(np.mean(h.predict(X_test) != Y_test))
	    print("Hamming loss: ", hamming_losses[-1])
	    durations.append(clock() - t0)

	print("Trained %d epochs in %d seconds." % (i,int(clock() - t0)))
	Y_pred = h.predict(X_test)
	print(Y_test)
	print(Y_pred)
	loss = np.mean(Y_pred != Y_test)
	print("Hamming loss: ", loss)
	overfitting.append(np.mean(h.predict(X_train) != Y_train))
	print("Overfitting hamming :",overfitting[-1])
	durations.append(clock() - t0)
	for o in range(len(durations) - 1):
		durations[o] = durations[o+1] - durations[o]
	durations.remove(durations[-1])

	legend = "layer size="+str(sizes[j])
	r, g, b = 1, 0, 0
	if (j%4 == 1):
		r,g,b = 0,1,0
	if (j%4 == 2):
		r,g,b = 0,0,1
	if (j%4 == 3):
		r,g,b = 1,1,0
	plt.plot(iterations, hamming_losses, color = (r,g,b), label=legend )
	all_hamming_losses.append(hamming_losses)
	all_duration.append([sizes[j], durations[:]])

plt.xlabel("Iteration")
plt.ylabel("Hamming Loss")
plt.legend()
plt.title("Hammering Loss for different layers sizes")
plt.savefig("Influence of hidden layer size.png")
plt.show()
print("Overfitting", sizes, overfitting)
# Influence of epochs size
"""
# Influence of number of hidden layers

"""
for j in range(len(n_layers)):
	t0 = clock()

	# Test our classifier 
	h = Network(n_hid_layer=n_layers[j])

	i = 0
	epochs = 200
	durations = []
	iterations = []
	hamming_losses = []

	while (clock() - t0) < 120:
	    h.fit(X_train,Y_train,n_epochs=epochs)
	    i = i + epochs
	    print("Current epoch :",i)
	    print("Time elapsed :",clock() - t0)
	    iterations.append(i)
	    hamming_losses.append(np.mean(h.predict(X_test) != Y_test))
	    print("Hamming loss: ", hamming_losses[-1])
	    durations.append(clock() - t0)

	

	print("Trained %d epochs in %d seconds." % (i,int(clock() - t0)))
	Y_pred = h.predict(X_test)
	print(Y_test)
	print(Y_pred)
	loss = np.mean(Y_pred != Y_test)
	print("Hamming loss: ", loss)
	print("Overfitting hamming :",np.mean(h.predict(X_train) != Y_train))
	durations.append(clock() - t0)
	for o in range(len(durations) - 1):
		durations[o] = durations[o+1] - durations[o]
	durations.remove(durations[-1])
	legend = "Number of layers="+str(n_layers[j])
	r, g, b = 1, 0, 0
	if (j%5 == 1):
		r,g,b = 0,1,0
	if (j%5 == 2):
		r,g,b = 0,0,1
	if (j%5 == 3):
		r,g,b = 1,1,0
	if (j%5 == 4):
		r,g,b = 1,0,1
	plt.plot(iterations, hamming_losses, color = (r,g,b), label=legend )
	all_hamming_losses.append(hamming_losses)
	all_duration.append([n_layers[j], durations[:]])

plt.xlabel("Iteration")
plt.ylabel("Hamming Loss")
plt.legend()
plt.title("Hammering Loss for different number of hidden layers")
plt.savefig("Influence of number of layers.png")
plt.show()
"""
# Checking durations with optimal parameters

t0 = clock()

# Test our classifier 
h = Network()
epochs = 10
i = 0
durations = []
hamming_losses = []

while ((clock() - t0) < 120):
    h.fit(X_train,Y_train)
    i = i + epochs
    #print("Current epoch :",i)
    durations.append(clock() - t0)
    #print("Time elapsed :",durations[-1])
    hamming_losses.append(np.mean(h.predict(X_test) != Y_test))
    #print("Hamming loss: ", hamming_losses[-1])

print("Trained %d epochs in %d seconds." % (i,int(clock() - t0)))
Y_pred = h.predict(X_test)
print(Y_test)
print(Y_pred)
loss = np.mean(Y_pred != Y_test)
print("Hamming loss: ", loss)
print("Overfitting hamming :",np.mean(h.predict(X_train) != Y_train))
durations.append(clock() - t0)

# Scikit-Learn classifiers

classifier1 = OneVsRestClassifier(SVC(kernel='linear'))
t0 = clock()
classifier1.fit(X_train,Y_train)
Y_pred_c1 = classifier1.predict(X_test)
t1 = (clock() - t0)
loss_1VsR = np.mean(Y_pred_c1 != Y_test)
print("OneVsRestClassifier, Hamming loss: ",loss_1VsR)

classifier2 = ClassifierChain(SVC(kernel='linear'))
t0 = clock()
classifier2.fit(X_train,Y_train)
Y_pred_c2 = classifier2.predict(X_test)
t2 = (clock() - t0)
loss_CC = np.mean(Y_pred_c2 != Y_test)
print("ClassifierChain, Hamming loss: ",loss_CC)

classifier3 = KNeighborsClassifier()
t0 = clock()
classifier3.fit(X_train,Y_train)
Y_pred_c3 = classifier3.predict(X_test)
t3 = (clock() - t0)
loss_KNC = np.mean(Y_pred_c3 != Y_test)
print("KNeighborsClassifier, Hamming loss: ",loss_KNC)

classifier4 = DecisionTreeClassifier()
t0 = clock()
classifier4.fit(X_train,Y_train)
Y_pred_c4 = classifier4.predict(X_test)
t4 = (clock() - t0)
loss_DCT = np.mean(Y_pred_c4 != Y_test)
print("DecisionTreeClassifier, Hamming loss: ",loss_DCT)

print("Hamming Loss")
print(np.mean(np.array(durations)))
print(t1,t2,t3,t4)