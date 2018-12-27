import tensorflow as tf

from tensorflow.examples.tutorials.mnist import input_data
mnist = input_data.read_data_sets("MNIST_data/", one_hot=True) # y labels are oh-encoded

# Getting data information
n_train = mnist.train.num_examples # 55,000
n_validation = mnist.validation.num_examples # 5000
n_test = mnist.test.num_examples # 10,000

# Initializing parameters
n_input = 784 # 28*28 pixels
n_output = 10 # 0 to 9 digits
n_hid_layer = 3
n_neurons = []
n_init = 1024 # Initial nulber of neurons per layer
for i in range(n_hid_layer):
    n_init = n_init // 2
    n_neurons.append(n_init)
