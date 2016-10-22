function [pos] =  GosseCenter(image_, opt_)
    
    halfCorrWindow =  opt_.halfCorrWindow;
    [x y] = size(image_);
    %get conv
    %x
	scorex =  zscore(sum(image_)); 
    convx  =  conv(scorex,scorex);  
    [~,posx] = max(convx );       
    
    startx = posx - halfCorrWindow; 
    if startx <=0
        startx =1;
    elseif startx > (2*x+1) - 2*halfCorrWindow
        startx = (2*x+1) - 2*halfCorrWindow;
    end
    endx   = startx + 2*halfCorrWindow;
  
    fx = polyfit(startx:endx,convx(startx:endx), 2);
    pos(1) = -fx(2)/(4*fx(1));
    
    %y
    scorey =  zscore(sum(image_,2)'); 
    convy  =  conv(scorey,scorey);    
    [~,posy] = max(convy);
    
    starty = posy - halfCorrWindow; 
    if starty <=0
        starty =1;
    elseif starty > (2*y+1) - 2*halfCorrWindow
        starty = (2*y+1) - 2*halfCorrWindow;
    end    
    endy   = starty + 2*halfCorrWindow;
 
    fy = polyfit(starty:endy,convy(starty:endy), 2);
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

