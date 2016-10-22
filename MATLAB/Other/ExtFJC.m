function [ ret ] = ExtFJC( Npb )
%EXTWLC Summary of this function goes here
%   Detailed explanation 6goes here
   

    contourLen = Npb*0.56;%0.63+-0.08
    F = 0.01:0.01:20;%pn
    
    
    xmin = 0;
    xinc = 0.5;
    xmax = 20;
    
    ymin = 0;
    yinc = 0.02;
    ymax = 0.6;%contourLen+contourLen*0.1;
    
    
    KbT = 4.2;
    b = 1.2;
    S = 800;%pN
    A = coth(b*F/KbT);
    B =  KbT./(b*F);
    C = 1+F/S;
    x =  contourLen *( A -B ).* C ;
    hold on;
    h1 = plot(F,x,'*r');
    grid on;
    axis([xmin xmax ymin ymax]);
    set(gca,'XTick',xmin:xinc:xmax);
    set(gca,'YTick',ymin:yinc:ymax);

end

