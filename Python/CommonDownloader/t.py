from scipy import optimize, special
from numpy import *
from pylab import *
from matplotlib import pyplot as plt
import numpy as np
from mpl_toolkits.mplot3d import Axes3D



    
def calcOTF(f_DeltaZo):
    f_DeltaZi = -1*f_Di+f_f*(f_Df+f_DeltaZo)/(f_Df+f_DeltaZo-f_f)
    
    w = -1*f_Di - f_DeltaZi*cos(f_ai)+sqrt((f_Di*f_Di+2*f_Di*f_DeltaZi+f_DeltaZi*f_DeltaZi*cos(f_ai)*cos(f_ai)))
     
    s = (f_lamda/sin(f_ai))*sqrt(u*u+v*v)
    
    x = (8*pi*w/f_lamda)*(1-abs(s)/2)*(abs(s)/2)
    
    AA = (1-0.69*s + 0.0076*s*s+0.043*s*s*s)*2

    OTFuvw =  AA*special.jn(1,x)/x
    return [s,OTFuvw]
    
M = 40
NA = 0.55
f_lamda = 0.0005

f_Df = 195/(M+1)
f_Di = f_Df*M
f_f = f_Di/(M+1)
f_ao = arcsin(NA)
f_A = f_Df*tan(f_ao)
f_ai = arctan(f_A/f_Di)

#print(f_Df,f_Di,f_f,f_ao,f_A,f_ai)


u = np.arange(-112.5,112.5,0.1)
v = np.arange(-112.5,112.5,0.1)
v = u*0
print(size(u))
 
f_DeltaZoR = np.arange(0.001,0.02,0.001)


radiu = 50
 
 
def test0():
    ind = 0
    f_DeltaZo = 0.01
    ind = ind+1
    fig = plt.figure()
    ax = Axes3D(fig)
    plt.cla()
    print(f_DeltaZo)
    print(str(ind)+"\r\n")
    [s, OTFuvw] = calcOTF(f_DeltaZo)
 
    X, Y = np.meshgrid(u, v)
    Z = OTFuvw
    ax.plot_surface(X, Y, Z, rstride=1, cstride=1)
    fftF = np.fft.fft(OTFuvw)[0:radiu]
    fftx = np.arange(0,radiu,radiu/size(fftF))
     
    #Plot3D(fftx,fftx,fftF,ax)
    plt.show()
def test1():
    ind = 0
    f_DeltaZo = 0.01
    ind = ind+1
    plt.cla()
    print(f_DeltaZo)
    print(str(ind)+"\r\n")
    [s, OTFuvw] = calcOTF(f_DeltaZo)
    subplot(211)
    plot(s,OTFuvw)
    fftF = np.fft.fft(OTFuvw)[0:radiu]
    fftx = np.arange(0,radiu,radiu/size(fftF))
    subplot(212)
    plot(fftx,fftF)
 
def test():
    ind = 0
    for f_DeltaZo in f_DeltaZoR:
        f_DeltaZo = 0.01
        ind = ind+1
        plt.cla()
        print(f_DeltaZo)
        print(str(ind)+"\r\n")
        [s, OTFuvw] = calcOTF(f_DeltaZo)
        fftF = np.fft.ifft(OTFuvw)
        fftx = np.arange(0,radiu,radiu/size(fftF))
        print(size(fftF))
        print(size(fftx))
        plot(fftx,fftF)
        plt.pause(0.001)
def mainOnce():
    test1()
    plt.pause(50)
def mainAuto():
    while True:
        test()
        plt.pause(0.05)

mainOnce()
