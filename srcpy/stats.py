# -*- coding: utf-8 -*-
"""
Created on Thu Nov  7 17:06:15 2019

@author: harold
"""

from os import listdir
from os import path
import csv

for file in listdir("./AV45_T1_FS_SKULL"):
    name = file[:file.find("_",1)]
    print(name)