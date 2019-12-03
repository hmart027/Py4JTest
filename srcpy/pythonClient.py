# -*- coding: utf-8 -*-
"""
Created on Tue Jul 23 18:32:19 2019

@author: harold
"""

class ImageProcessor(object):
    
    def processImageTest(self):
        if hasattr(self, 'i') is False:
            self.i = 0
        self.i = self.i+1
        return self.i
        
    def processImage(self):
        start_time = time.time()
        # Load image
        self.imgFile = open('/tmp/vision.mon', 'rb')
        self.imgFile.seek(0)
        img = Image.GetRootAsImage(bytearray(self.imgFile.read()), 0)
        self.image = np.reshape(img.DataAsNumpy(), (img.Height(),img.Width(),3), 'C')
        elapsed_time = time.time() - start_time
        print("Loaded in "+str(round(elapsed_time,3))+" s")
        # Run detection
        start_time = time.time()
        #tf.keras.backend.clear_session()
        results = model.detect([self.image]) 
        # Visualize results
        self.r = results[0]    
        elapsed_time = time.time() - start_time
        print("Found: "+str(self.r['rois'].shape[0])+" objects in "+str(round(elapsed_time,3))+" s")
        
        return self.r['rois'].shape[0]
    
    def processImage2(self, w=None, h=None, c=None, data=None):
        if data is None:
            return 0
        #print("Img Dims: " + str(w)+", "+str(h)+", "+str(c))
        start_time = time.time()
        # Load image
        self.image = np.reshape(np.frombuffer(data, dtype='B', count=(w*h*c)), (h,w,c), 'C')
        # Run detection
        #tf.keras.backend.clear_session()
        results = model.detect([self.image]) 
        # Visualize results
        self.r = results[0]    
        elapsed_time = time.time() - start_time
        print("Found: "+str(self.r['rois'].shape[0])+" objects in "+str(round(elapsed_time,3))+" s")
        
        return self.r['rois'].shape[0]
    
    def getScore(self, id_int):
        return (float)(self.r['scores'][id_int])
    
    def getLabel(self, id_int):
        return class_names[self.r['class_ids'][id_int]]
	
    def getObjectBytes(self, id_int):
        return np.ndarray.flatten(self.r['rois'][id_int]).tobytes()
	
    def getMaskBytes(self, id_int):
        return np.ndarray.flatten(self.r['masks'][:,:,id_int]).tobytes()
    
    def getData(self):
        return self.r['rois'].tobytes() + self.r['class_ids'].tobytes() + self.r['scores'].tobytes() + self.r['masks'].tobytes()

    class Java:
        implements = ["com.martindynamics.py4j.test.ImageProcessorListener"]

import os
import time
#os.environ['CUDA_VISIBLE_DEVICES'] = '-1'
import sys
#from py4j.java_gateway import JavaGateway, CallbackServerParameters
from py4j.clientserver import ClientServer, JavaParameters, PythonParameters
from random import shuffle
import numpy as np
import skimage.io
import matplotlib
import matplotlib.pyplot as plt
from keras.backend import clear_session
import tensorflow as tf
import keras
import flatbuffers
from Image import Image

processor = ImageProcessor()
gateway = ClientServer(
    java_parameters=JavaParameters(),
    python_parameters=PythonParameters(),
    python_server_entry_point=processor)
#gateway = JavaGateway(
#    callback_server_parameters=CallbackServerParameters(),
#    python_server_entry_point=processor)

# Root directory of the project
ROOT_DIR = os.path.abspath("/mnt/Research/Harold/software/Mask_RCNN/")

# Import Mask RCNN
sys.path.append(ROOT_DIR)  # To find local version of the library
from mrcnn import utils
import mrcnn.model as modellib
from mrcnn import visualize
# Import COCO config
sys.path.append(ROOT_DIR)  # To find local version
import coco

# Directory to save logs and trained model
MODEL_DIR = os.path.join(ROOT_DIR, "logs")

# Local path to trained weights file
COCO_MODEL_PATH = os.path.join(ROOT_DIR, "mask_rcnn_coco.h5")
# Download COCO trained weights from Releases if needed
if not os.path.exists(COCO_MODEL_PATH):
    utils.download_trained_weights(COCO_MODEL_PATH)

# Directory of images to run detection on
IMAGE_DIR = os.path.join(ROOT_DIR, "images")

class InferenceConfig(coco.CocoConfig):
    # Set batch size to 1 since we'll be running inference on
    # one image at a time. Batch size = GPU_COUNT * IMAGES_PER_GPU
    GPU_COUNT = 1
    IMAGES_PER_GPU = 1
    IMAGE_MAX_DIM  = 640
    IMAGE_MIN_DIM  = 480

config = InferenceConfig()
#config.display()

#Take out for GPU use
#config2 = tf.ConfigProto() 
#config2.inter_op_parallelism_threads = 1 
#keras.backend.set_session(tf.Session(config=config2))

# Create model object in inference mode.
model = modellib.MaskRCNN(mode="inference", model_dir=MODEL_DIR, config=config)

# Load weights trained on MS-COCO
model.load_weights(COCO_MODEL_PATH, by_name=True)

model.keras_model._make_predict_function()

# COCO Class names
# Index of the class in the list is its ID. For example, to get ID of
# the teddy bear class, use: class_names.index('teddy bear')
class_names = ['BG', 'person', 'bicycle', 'car', 'motorcycle', 'airplane',
               'bus', 'train', 'truck', 'boat', 'traffic light',
               'fire hydrant', 'stop sign', 'parking meter', 'bench', 'bird',
               'cat', 'dog', 'horse', 'sheep', 'cow', 'elephant', 'bear',
               'zebra', 'giraffe', 'backpack', 'umbrella', 'handbag', 'tie',
               'suitcase', 'frisbee', 'skis', 'snowboard', 'sports ball',
               'kite', 'baseball bat', 'baseball glove', 'skateboard',
               'surfboard', 'tennis racket', 'bottle', 'wine glass', 'cup',
               'fork', 'knife', 'spoon', 'bowl', 'banana', 'apple',
               'sandwich', 'orange', 'broccoli', 'carrot', 'hot dog', 'pizza',
               'donut', 'cake', 'chair', 'couch', 'potted plant', 'bed',
               'dining table', 'toilet', 'tv', 'laptop', 'mouse', 'remote',
               'keyboard', 'cell phone', 'microwave', 'oven', 'toaster',
               'sink', 'refrigerator', 'book', 'clock', 'vase', 'scissors',
               'teddy bear', 'hair drier', 'toothbrush']

#matplotlib.use('Qt5Agg')
#visualize.display_instances(processor.image, processor.r['rois'], processor.r['masks'], processor.r['class_ids'], class_names, processor.r['scores'])