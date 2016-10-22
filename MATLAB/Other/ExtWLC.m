function [ ret ] = ExtWLC( Npb )
%EXTWLC Summary of this function goes here
%   Detailed explanation goes here
   

    contourLen = Npb*0.34;
    l = 0.01:0.001:contourLen;
    x = l/contourLen;
    
    xmin = 0;
    xinc = 0.5;
    xmax = 20;
    
    ymin = 0;
    yinc = 0.01;
    ymax = 0.4;%contourLen+contourLen*0.1;
    
    a2 = -0.5164228;
    a3 = -2.737418;
    a4 = 16.07497;
    a5 = -38.87607;
    a6 = 39.49944;
    a7 = -14.17718;
    
    KbT = 4.2;
    p = 50; 
    F = (KbT/p)*( x - 0.25 + 0.25*(1-x).^(-2) + a2*(x.^2) + a3*(x.^3) +a4*(x.^4) +a5*(x.^5) +a6*(x.^6) +a7*(x.^7));
    
    h1 = plot(F,l,'*r');
    hold on;
    h2 = plot(F,l);
    
    grid on;
    axis([xmin xmax ymin ymax]);
    set(gca,'XTick',xmin:xinc:xmax);
    set(gca,'YTick',ymin:yinc:ymax);

end

