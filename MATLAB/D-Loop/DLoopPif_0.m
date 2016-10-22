close all;
clear all;
nBow = 60;
nChordStart = 36;
nChordEnd = 56;
radius = 5:0.01:25;
arcLength = nBow*0.34;
temp = arcLength./(2.*radius);
X = 2*radius.*sin(arcLength./(2*radius));
T = 2*200*temp./(radius.*radius)./(2*sin(temp) - 2.*temp.*cos(temp) );

plot(T,X); hold on;
grid on;

T1 = 5:0.01:10;

i=0;
for nNum = nChordStart:2:nChordEnd %弦长 一步增加2
    i = i+1;
    X1 = nNum*0.63.*(coth( T1*1.5/4.2 ) -4.2./(T1*1.5)).*(1+T1/800);
    plot(T1,X1);
    index= myFindRoot(X1,T1,X,T);
    plot(T1(index),X1(index),'*');
    
    T_nNum = T1(index);
    length = X1(index)/nNum;
    distance = length*(nNum-30);
    FRET = 1/(1+(distance/6)^6) ;
    out(i,:) = [nNum,nNum-nChordStart,T_nNum,length,distance,FRET];
end
title = ['nNum','nNum-nChordStart','T_nNum','length','distance','FRET']
out
hold off;