import tensorflow as tf
import numpy as np 

class tf_network():

    # Default parameters
    alpha = 0.01 # Learning rate
    hls = 240 # Number of neurons per layer
    n_lay = 2 # Number of hidden layers

    def __init__(self, learning_rate=alpha, hidden_layer_size=hls, n_hid_layer=n_lay):

        # Initializing attributes
        # Initializing the attributes of the network
        self.alpha = learning_rate
        self.batch_size = 60

        self.i_n = 6112
        self.n_classes = 18

        self.hid_lay_size = hidden_layer_size
        self.nhl = n_hid_layer

        self.X = tf.placeholder("float", [None, self.i_n])
        self.Y = tf.placeholder("float", [None, self.n_classes])
        
        self.weights = []
        self.biases = []

        # Initializing the network layers
        for i in range(self.nhl +1):
            inp = hidden_layer_size
            outp = hidden_layer_size
            if (i == 0):
                inp = self.i_n
            if (i == self.nhl):
                outp = self.n_classes
            self.weights.append(tf.Variable(tf.random_normal([inp, outp])))
            self.biases.append(tf.Variable(tf.random_normal([outp])))

        # Computing through the network

        self.last = self.X

        for i in range(self.nhl + 1):
            self.last = tf.nn.sigmoid( tf.add( tf.matmul(self.last,self.weights[i]) , self.biases[i] )  )


        self.logits = tf.nn.softmax(self.last)
        self.cost = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(labels=self.Y,logits=self.logits))
        optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate)
        self.op = optimizer.minimize(self.cost)

        self.is_initialized = False
        self.init = tf.global_variables_initializer()
        self.sess = tf.Session()


    def fit(self,X,Y,warm_start=True,n_epochs=10):
        ''' train the network, and if warm_start, then do not reinit. the network
            (if it has already been initialized)
        '''
        if not(warm_start and self.is_initialized):
            self.sess.run(self.init)
            self.is_initialized = True
            print("Network initialized")

        for epoch in range(n_epochs):
            _,cost = self.sess.run([self.op,self.cost],feed_dict={self.X: X,self.Y: Y})

        return self


    def predict_proba(self,X):
        ''' return a matrix P where P[i,j] = P(Y[i,j]=1), 
        for all instances i, and labels j. '''
        # TODO
        return self.sess.run(self.last,feed_dict={self.X: X})

    def predict(self,X):
        ''' return a matrix of predictions for X '''
        return (self.predict_proba(X) >= 0.5).astype(int)