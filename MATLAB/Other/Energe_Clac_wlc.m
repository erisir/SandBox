function [ret ] = Energe_Clac_wlc( )
close all;
intEnd = 0.51;
ntNum = 1;
p_len_dsDNA = 1.5;
c_len_dsDNA = 0.56*ntNum;
presision = 0.0001;
x = presision:presision:(intEnd*ntNum);

F_WLC = (4.2/p_len_dsDNA) * (x/c_len_dsDNA + 0.25*(1-x/c_len_dsDNA).^(-2) - 0.25);
  
h1=plot(x,F_WLC,'r-');
hold on;



sumEnd = intEnd*ntNum;
sumStart = 0.34;

sumEndInd = sumEnd/presision;
sumStartInd = sumStart/presision;

sumData = F_WLC(sumStartInd:sumEndInd);
sumInd = x(sumStartInd:sumEndInd);
Total = sum(sumData)*presision;

plot(sumInd,sumData,'g*');
plot(sumInd,sumData,'go');
bar(sumInd,sumData);

ymin = 0;
yinc = 1;
ymax = F_WLC(sumEndInd)+10;

xmin = 0;
xinc = 0.02;
xmax = sumEnd+0.05;

str = ['single[S]= ' num2str(Total) ' Total =s*' num2str(ntNum) '=' num2str(Total*ntNum) '  Force='  num2str(F_WLC(sumEndInd))];
text(sumEnd, F_WLC(sumEndInd)+2,str,'HorizontalAlignment','center','BackgroundColor',[.7 .9 .7]);

WLCstr = ['WLC: P=' num2str(p_len_dsDNA) '  L0=' num2str(c_len_dsDNA) ];

legend(h1,WLCstr);
axis([ xmin xmax ymin ymax]);

set(gca,'XTick',xmin:xinc:xmax);
set(gca,'YTick',ymin:yinc:ymax);

% dW = F*dL

grid on;
ret = Total;
end