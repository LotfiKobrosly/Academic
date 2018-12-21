# Keras Classifier

# Importing libraries
from keras.models import Sequential
from keras.layers import Dense
import numpy as np

# Create model
model = Sequential()
model.add(Dense(20, input_dim=400, activation='relu'))
model.add(Dense(20, activation='sigmoid'))
model.add(Dense(18, activation='sigmoid'))

# Compiling model
model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
