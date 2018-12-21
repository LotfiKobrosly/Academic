import tensorflow as tf
import numpy as np 

class Network():

	# Default parameters
    alpha = 0.01 # Learning rate
    hls = 240 # Number of neuros per layer
    n_lay = 2 # Number of hidden layers

	def __init__(self, learning_rate=alpha, hidden_layer_size=hls, n_hid_layer=n_lay):

		# Initializing attributes

