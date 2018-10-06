from numpy import *
from numpy.linalg import norm


def furthest_neighbour(knn_list,x):

    #Initializing output
    i,i_v,d = 0, knn_list[0][0], knn_list[0][1]

    #Searching through the list
    for j in range(1,len(knn_list)):

        if (knn_list[j][1]>d):
            i,i_v,d = j, knn_list[j][0], knn_list[j][1]

    return(i,i_v,d)



def  kNN_prediction(k, X, labels, x):
    
    #distances list
    distances = [norm(X[i] - x) for i in range(len(X))]

    #distances sorting
    indexes = argsort(distances)

    #kNN list
    knn_list = indexes[:k]

    #Computing the average label
    s = 0

    for i in knn_list:
        s += labels[i]

    s = s/k

    #Finding the nearest value
    label = labels[knn_list[0]]
    for l in knn_list:
        if (abs(s-label) > abs(s-labels[l])):
            label = labels[l]
    
    return label

def kNN_prediction_second(k, X, labels, x):
    '''
    kNN classification of x
    -----------------------
        Input: 
        k: number of nearest neighbors
        X: training data           
        labels: class labels of training data
        x: test instance

        return the label to be associated with x

        Hint: you may use the function 'norm' 
    '''

    # TASK
    
    #List of k Nearest Neighbours
    knn_list = []

    #Initialising knn_list
    for i in range(k):
        knn_list.append([i,norm(x-X[i])])

    #Getting the furthest neighbour of x
    i,i_v,d = furthest_neighbour(knn_list,x)

    #Searching for k nearest neighbours
    for j in range(len(X)):
        if (norm(X[j]-x) < d):
            knn_list[i] = [j,norm(X[j]-x)]
            i,i_v,d = furthest_neighbour(knn_list,x)

    #Getting the preponderant attribute

    """
    #Counting the occurences of each label in the knn_list
    
    labels_occ = [0 for j in range(len(labels))]
    i_m, m = 0, 0
    for j in range(len(knn_list)):
        labels_occ[knn_list[j][0]] += 1
        if (labels_occ[knn_list[j][0]] > m ):
            m = labels_occ[knn_list[j][0]]
            i_m = knn_list[j][0]
    """

    #Computing the average label
    s = 0

    for y in knn_list:
        s += labels[y[0]]

    s = s/k

    #Finding the nearest value
    label = labels[0]
    for l in labels:
        if (abs(s-label) > abs(s-l)):
            label = l
    
    return label

 
