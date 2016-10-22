function [pos] =  GosseCenter(image_, opt_)
    %get conv
   
    %x
  	sumx   =  sum(image_); 
    [x y] = size(image_);
    
	scorex =  zscore(sumx); 
	convx  = conv(scorex,scorex);
    [~,posx] = max(convx);       
    
    startx = posx - opt_.halfCorrWindow; 
    if startx <=0
        startx =1;
    elseif startx > (2*x+1) - 2*opt_.halfCorrWindow
        startx = (2*x+1) - 2*opt_.halfCorrWindow;
    end
    endx   = startx + 2*opt_.halfCorrWindow;
    x = startx:endx;
 
    fx = polyfit(x,convx(startx:endx), 2);
    pos(1) = -fx(2)/(4*fx(1));
    
    %y
    
    sumy   =  sum(image_,2)'; 
    scorey =  zscore(sumy); 
    convy  =  conv(scorey,scorey);    
    [~,posy] = max(convy);
    
    starty = posy - opt_.halfCorrWindow; 
    if starty <=0
        starty =1;
    elseif starty > (2*y+1) - 2*opt_.halfCorrWindow
        starty = (2*y+1) - 2*opt_.halfCorrWindow;
    end    
    endy   = starty + 2*opt_.halfCorrWindow;

    y = starty:endy;
    
    fy = polyfit(y,convy(starty:endy), 2);
    pos(2) = -fy(2)/(4*fy(1));
   
      
   %show  if flag ==1 :raw data track mode
   
	if opt_.showimage == 1
        %convx
        subplot(2,4,1);
 		plot(convx,'.g');   
        xlabel('x','Fontsize',16);
		ylabel('convx','fontsize',16);
		title('convx','fontsize',10)
		grid on
 	    %convy
        subplot(2,4,5);
		plot(convy,'.g');         
		xlabel('y','Fontsize',16);
		ylabel('convy','fontsize',16);
		title('convy','fontsize',10)
		grid on
        %sumx
        subplot(2,4,2);
		plot(scorex,'.');
		xlabel('x','Fontsize',16);
		ylabel('sumx','fontsize',16);
		title('sumx','fontsize',10)
		grid on        
        %sumy
		subplot(2,4,6);
		plot(scorey,'.');
		xlabel('y','Fontsize',16);
		ylabel('sumy','fontsize',16);
		title('sumy','fontsize',10)
		grid on
        %image and center
        subplot(2,4,8);	
        hold on
        imshow(image_);
        plot(posx,posy,'*r');
        hold off
        grid on       
	end          
end

