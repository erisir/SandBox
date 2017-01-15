
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import  *
import serial  #pip install pyserial
import sys
import time
import random
import binascii,encodings; 
from sympy.strategies.core import switch
_U_SetVotage     = '0';
_U_SetPTerm      = '1';
_U_SetITerm      = '2';
_U_SetDTerm        = '3';
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
_U_SetPIDMode= 'b';
value = 65534

cmd = _U_GetVotage
High = int(((value/256)%256))
Low = int((value%256))
buf = bytes([ord('@'),ord(cmd),High,Low])
comm = None
try:   
    comm = serial.Serial("COM4",115200)              
except:
    print("串口被其他程序占用")
comm.write(buf) 
res=comm.read_until()
print(res)
comm.close()