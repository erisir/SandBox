function [ ret ] = FRET_Analysis( filename,bin,bin1 ,baseLine)
%FRET_ANALYSIS Summary of this function goes here
%   Detailed explanation goes here
data = dlmread(filename);
FRET = data; 
d1 = FRET(:,1);


index1 = [find(d1>baseLine)];
FRET(index1,:) = [];
deltaFRET = FRET(:,2) - FRET(:,1);%
deltaFRET( find(deltaFRET < 0)) = [];%只要拉开的部分，
 

dlmwrite('deltaFRET.csv',abs(deltaFRET));


subplot(2,1,1);
FRET0 = data;
f1 = FRET0(:,1);
FRET0(find(f1>baseLine),:) = [];
[x y] = size(FRET0);
hist(reshape(FRET0,1,x*y),bin);grid on;
%set(gca,'XMinorTick',[0:0.05:1]);
set(gca,'XMinorTick','on','XTick',[0:0.05:1]);
subplot(2,1,2);
FRET1 =  data;

FRET1(find(FRET1>1)) = 1;
R = 6*(1./FRET1-1).^(1/6);
deltaR = R(:,1) - R(:,2);
deltaR(find(deltaR<0)) = [];
hist(deltaR,bin1);grid on;
set(gca,'XTick',[0:0.2:10]);
ret = abs(deltaFRET);

end

