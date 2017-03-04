
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
import UIComm,UIControl,UIDetail,UIOther
import matplotlib.pyplot as pl
import serial  #pip install pyserial
import sys
import time
import random
import binascii,encodings; 
import numpy as np
from sympy.strategies.core import switch
from struct import pack,unpack
from scipy.optimize import curve_fit  
a = [40,20,10,4]
b = [1.92,6.16,6.91,15.1]
def func(x, a, b, c):  
    return a * np.exp(-b * x) + c  
#ForwardFunc = np.polyfit(np.array(a),np.array(b) , 1)#用2次多项式拟合
popt, pcov =curve_fit(func, a, b)  
pl.cla()
pl.grid() #开启网格
ax = pl.gca()
pl.xlabel("PWM")
pl.ylabel("Votage")
pl.title("PWM => Votage")
pl.legend()

pl.plot(a, b, 'b*')
pl.hold(True)
fitX = range(2,100,10)  
y2 = [func(i, popt[0],popt[1],popt[2]) for i in fitX]  
pl.plot(fitX,y2,'r--')  
 
pl.show()