from struct import pack,unpack
import serial,time
from PyQt5 import  QtGui
from matplotlib.backends.backend_qt5agg import  FigureCanvasQTAgg as FigureCanvas
from matplotlib.backends.backend_qt5agg import NavigationToolbar2QT as NavigationToolbar
from matplotlib.figure import Figure
import numpy as np
from array import array
import time
import random
import threading
from datetime import datetime
from matplotlib.dates import  date2num, MinuteLocator, SecondLocator, DateFormatter,\
    num2date
from PyQt5.QtGui import *  
from PyQt5.QtGui import *  
from PyQt5.QtCore import *  
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  * 
_U_SetVotage     = '0';
_U_SetPTerm      = '1';
_U_SetITerm      = '2';
_U_SetDTerm      = '3';
_U_SetDura       = '4';
_U_SetPWMVal     = '5';
_U_GetVotage     = '6';
_U_SetTClose     = '7';
_U_SetTOpen      = '8';
_U_SetTPID       = '9';
_U_SetVotageTimes= 'a';
_U_SetPIDMode= 'b';
_U_SetPIDPeriod = 'c';
_U_SetTIM4Prescaler = 'd';
_U_SetPIDVotageChanel =  'e'
_U_SetForwardA = 'f';
_U_SetForwardB = 'g';
_U_SetForwardC = 'h';
_U_SetBackwardA = 'i';
_U_SetBackwardB = 'j';
_U_SetBackwardC = 'k';
_U_SetPIDThredHold='l';

 
def setPWM(comm,value):
    byteArr = pack('f',value)
    checkSum = 'X'
    buf = bytes([ord('@'),ord(_U_SetPWMVal),byteArr[0],byteArr[1],byteArr[2],byteArr[3],ord(checkSum)])
    comm.write(buf)
    time.sleep(0.1)
    res = comm.read_until()
    print(res)   
def getVotage(comm):
    byteArr = pack('f',0)
    checkSum = 'X'
    buf = bytes([ord('@'),ord(_U_GetVotage),byteArr[0],byteArr[1],byteArr[2],byteArr[3],ord(checkSum)])
    comm.write(buf)
    time.sleep(0.1)
    res = comm.read_until()
    print(res)     
comm = serial.Serial("COM4",int(115200))   
for x in range(2000,65535,20000):
    continue
    setPWM(comm,x)
    getVotage(comm)
    time.sleep(0.01)
    print('*'*10)
 
#res = comm.read_until()
#print(res)
comm.close()