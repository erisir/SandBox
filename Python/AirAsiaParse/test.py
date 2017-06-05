import requests
import re
import sys
from bs4 import BeautifulSoup
from collections import deque
import Conn
import random
import json
from _winapi import NULL
import urllib  
import os
import time
import unicodedata
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtCore import QDate
import datetime

date = QDate(2017, 5, 29)
 
depDate = datetime.date(date.year(), date.month(),date.day()) 

counter= 0
queue = deque()    
for x in range(-5,5):
    counter = counter+1
    depDateStr = (depDate + datetime.timedelta(days=x)).strftime("%Y-%m-%d")
    queue.append(counter)
print(queue)
print(queue[2])



