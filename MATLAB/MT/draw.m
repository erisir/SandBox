function   draw(data,axi,flag )
%DRAW Summary of this function goes here
%   Detailed explanation goes here
 if  flag  ==1         
        myxindex = axi(1);
        myxscale = axi(2);
        myyindex = axi(3);
        myyscale = axi(4);
        
        datasize = size(data,2);
        if myxindex >(datasize - floor(myxscale/2))
            myxindex = datasize - floor(myxscale/2);
        end
        if myxindex<floor(myxscale/2)
            myxindex = floor(myxscale/2);
        end
        startx = myxindex  - floor(myxscale/2);
        endx = myxindex  + floor(myxscale/2);
        x = startx:endx;
        plot(x,data(1,startx:endx),'.g');	      
        axis([(myxindex-myxscale),(myxindex+myxscale),(myyindex-myyscale),(myyindex+myyscale)]);
        title('ZIndexMeasure','fontsize',10)	        	
        xlabel('time','Fontsize',16);
		ylabel('zPosition/um','fontsize',16);
        grid on

 end
 
 if flag ==2
     imshow(data,[]);
 end

end

