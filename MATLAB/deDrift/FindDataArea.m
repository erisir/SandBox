function [ ret ] = FindDataArea(data,Rate,lowGate,highGate,NosieTolerence)
%FINDDATAARER Summary of this function goes here
%   Detailed explanation goes here
d = fdesign.bandpass('N,F3dB1,F3dB2',10,lowGate,highGate,Rate);  % Bandpass filter for the ECG
Hd = design(d,'butter');
passdata = filter(Hd,data);

Darea = find(abs(passdata)>NosieTolerence);
 
jumpPoint = find(diff(Darea)>250);

 
index = [ Darea(jumpPoint);Darea(jumpPoint+1); max(Darea)];
index(1) =[]; 

if size(index,1)>2
ret =  reshape(sort(index),2,size(index,1)/2);
end
if size(index,1)<2
    ret =[];
end

flag = 1;
if flag == 1
len = size(data,1);
x = 1:len;
hold on;
plot(x,data,'k',x,passdata,'r');
plot(index,data(index),'r*');

end


end

