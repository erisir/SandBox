

pImage = getDiffractionImg(1.3,0);
 R = (0 : 1 :100)'; 
 len = size(R,1);
 calProfile_ = zeros(1,len);
 calProfile_(1) = pImage(150,150);
 sheight = 30;
 Radius =100;
 if a == 1 
for i = 2:len
    dTheta = 1/abs(R(i));
    nTheta = 2*3.1415926/dTheta;		
    for j =0:nTheta
        x = (150+R(i)*cos(dTheta*j));						
        y = (150+R(i)*sin(dTheta*j));
        x0 = floor(x);
        y0 = floor(y);
        x1 = x0 +1;
        y1 = y0 +1;
        dx = x - x0;
        dy = y - y0;
        profSum = (1-dx)*(1-dy)*pImage(y0+ (x0-1)*300) + dx*(1-dy)*pImage(y0+(x1-1)*300) + (1-dx)*dy*pImage(y1+(x0-1)*300) + dx*dy*pImage(y1+ (x1-1)*300);						
        calProfile_(i) =  calProfile_(i)+profSum/nTheta;
    end
                    
end

 else
    index_1 =((150 - sheight/2 +j)*300 +150-Radius+i);
   end  
calProfile_ = zscore(calProfile_);
plot(calProfile_,'.r');