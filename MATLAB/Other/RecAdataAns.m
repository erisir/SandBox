function [ ret ] = RecAdataAns( fname ,lamda,binning,yfactor)
%RECADATAANS Summary of this function goes here
%   Detailed explanation goes here
close all;
data = getDelta(dlmread(fname));
dlmwrite( [ fname '.delta'],data);
sEnd = 30;
x = 1:sEnd;
y = poisspdf(x,lamda);
y = yfactor*y*(1/max(y));
h1 = plot(x,y,'r');
hold on;
[n,xout] = hist(data,1:binning:sEnd);
n(35:49) = 0;
n = n*(1/max(n));
h2 = bar(xout, n) ;
xmin = 0;
xinc = 3;
xmax = sEnd;

ymin = 0;
ymax = max(y)+max(y)/3;
yinc = 0.1;

xx=0:0.1:sEnd;
miu = 12.2;
sigma = 4;
ygs=gaussmf(xx,[sigma miu]);
ygs = ygs/8.7;
%h3 = plot(xx,ygs);
possonStr = ['Poisson:λ=' num2str(lamda) ];
gusStr = ['Gauss distribution: u=' num2str(miu) 'sigma = ' num2str(sigma)  ];
dataStr = ['data distribution: binning=' num2str(binning)];

soA = sort(data);
num = max(size(data));

qu = soA(round(num/4));
mid = soA(round(num/2));
qu3 = soA(3*round(num/4));
hold on;
%h4 = plot([qu mid qu3],[0.034,0.034,0.034],'r*');
%zwsStr = ['1/4分位' num2str(qu) '中位' num2str(mid) '3/4分位' num2str(qu3)];

%legend([h1,h2,h3,h4]',possonStr,dataStr,gusStr,zwsStr);
legend([h1]',possonStr);
axis([xmin xmax ymin ymax]);
set(gca,'XTick',xmin:xinc:xmax);
set(gca,'YTick',ymin:yinc:ymax);
grid on;
ret = data;

end

