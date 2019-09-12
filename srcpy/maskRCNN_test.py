# -*- coding: utf-8 -*-
"""
Created on Thu Jul 25 10:15:46 2019

@author: harold
"""

import os
#os.environ['CUDA_VISIBLE_DEVICES'] = '-1'
import sys
import time
from random import shuffle
import numpy as np
import skimage.io
import matplotlib
import matplotlib.pyplot as plt
from keras.backend import clear_session
import tensorflow as tf
import keras

# Root directory of the project
ROOT_DIR = os.path.abspath("/mnt/Research/Harold/software/Mask_RCNN/")

# Import Mask RCNN
sys.path.append(ROOT_DIR)  # To find local version of the library
from mrcnn import utils
import mrcnn.model as modellib
from mrcnn import visualize
# Import COCO config
#sys.path.append(ROOT_DIR)  # To find local version
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
    GPU_COUNT      = 1
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

# Load image
image = skimage.io.imread(os.path.join(IMAGE_DIR, "1045023827_4ec3e8ba5c_z.jpg"))

for i in range(0,100):
    tf.keras.backend.clear_session()
    # Run detection
    start_time = time.time()
    r = model.detect([image])[0]
    elapsed_time = time.time() - start_time
    # Visualize results
    print("Found: "+str(r['rois'].shape[0])+" objects in "+str(elapsed_time)+" seconds")
    
visualize.display_instances(image, r['rois'], r['masks'], r['class_ids'], 
                        class_names, r['scores'])