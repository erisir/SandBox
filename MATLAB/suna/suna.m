function [ ret ] = suna( rInterStep,polarFactor )
%SUNA Summary of this function goes here
%   Detailed explanation goes here
    close all;
    xpos=1385;
    ypos=1195;
    %[data map] = imread('6-D1.bmp');
    %image=ind2gray(data ,map);% IæÕ «ª“∂»æÿ’Û.
    image = imread('6-D1.tif');
    imageWidth = 2048;
    skipStart = 1;%140;
    beanRadiuPixel = 800;  
    image1 = image;
    ind = 1;
    
   %  return; 
    
    xAxis = skipStart:rInterStep:beanRadiuPixel;
    for  i = skipStart/rInterStep:beanRadiuPixel/rInterStep
      
         sumr = 0;
         r =i* rInterStep;          
         dTheta = polarFactor/r;
		 nTheta =fix(3/dTheta);
				 
        for Theta =0:nTheta;
             x = (xpos+ r*cos(Theta*dTheta+0.6));
             y = (ypos+ r*sin(Theta*dTheta+0.6));
             x0 = fix(x);
             y0 = fix(y);
             x1 = x0 +1;
             y1 = y0 +1;
             dx = x - x0;
             dy = y - y0;

            S00 =  image(x0 + y0* imageWidth);
            S01 =  image(x1 + y0* imageWidth);
            S10 =  image(x0 + y1* imageWidth);
            S11 =  image(x1 + y1* imageWidth);
            Sxy = double(S00*(1-dx)*(1-dy)+S01*dy*(1-dx)+S10*dx*(1-dy) +S11*dx*dy);
            Sxy = Sxy/nTheta;
            sumr = sumr+Sxy;
            image1(x0 + y0* imageWidth) = 255;
            image1(x1 + y1* imageWidth) = 255;
        end
        profile(ind) =double(sumr*r);
        ind = ind+1;
    end
  dlmwrite('x.txt',xAxis);
  dlmwrite('y.txt',profile);
 subplot(2,2,2);
 imshow(image1,[]);hold on;
 subplot(2,2,4);
  
  plot(xAxis,profile,'r-');hold on;
  subplot(2,2,3);
  plot(image((xpos+ypos*imageWidth):(xpos+ypos*imageWidth+beanRadiuPixel)),'g-');hold off;
  subplot(2,2,1);
   image((xpos+ypos*imageWidth):(xpos+ypos*imageWidth+beanRadiuPixel) )= 255;
   imshow(image,[]);hold on; 
end

