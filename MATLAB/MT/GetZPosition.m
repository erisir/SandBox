function [pos detal] = GetZPosition(draw_, image_,roi_ ,opt_,calProfile_, calPos_ )
%GETCZPOSITION Calculate the z position according to the provided calibration curve set.
    %input prase
	%opt.Radius            = opt_(1);
	%opt.RInterstep        = opt_(2);
	%opt.halfCorrWindow    = opt_(3);
	halfCorrWindow  =      opt_(4);  
	%opt.xFactor           = opt_(5);
	%opt.yFactor           = opt_(6);  
	opt.showimage         = opt_(7);
    opt.method            = opt_(8);
    opt.imgwidth          = opt_(9);
    opt.imgheight         = opt_(10);
  
    %roi.x       = roi_(1);
    %roi.y       = roi_(2);
    %roi.width   = roi_(3);
    %roi.height  = roi_(4);

    image = reshape(image_,opt.imgwidth,opt.imgheight);
    %get x,y and posprofile    
    [posProfile pos]= GetCalibration(image,roi_,opt_);   
    % Correlation with calibration profiles
    lenPf = size(calProfile_, 1);
    cr = zeros(lenPf, 1);
   
    for j = 1:lenPf              
        tmp = corrcoef(calProfile_(j,:), posProfile(1,:));       
        cr(j) = tmp(1,2);              
    end
    [~,indx] = max(cr); 
    
    fitStart = indx - halfCorrWindow;
    if fitStart <= 0
        fitStart = 1;        
    elseif fitStart > lenPf - 2*halfCorrWindow;
        fitStart = lenPf - 2*halfCorrWindow;        
    end  
    fitEnd = fitStart + 2*halfCorrWindow;   
          
    tmpX = calPos_(fitStart:fitEnd)' ; 
    tmpY = cr(fitStart:fitEnd)  ;                  
    p = polyfit(tmpX, tmpY, 2);        
    pos =[pos -p(2)/(2*p(1))]; %get max index(x=1b/2a)       
    detal = [1 2 3];
    
    %show it if flag == 2:z index track ,working mode
    if  opt.showimage  ==2 
        
        myxindex = draw_(1);
        myxscale = draw_(2);
        myyindex = draw_(3);
        myyscale = draw_(4);
        
        hold on
        %subplot(2,1,2);
        plot(myxindex,pos(1,3),'.g');	      
        axis([(myxindex-myxscale),(myxindex+myxscale),(myyindex-myyscale),(myyindex+myyscale)]);
        title('CalProfile','fontsize',10)	        	
        xlabel('time','Fontsize',16);
		ylabel('zPosition/um','fontsize',16);
        grid on
        hold off
    end
    %show it if flag == 1:raw data track mode
    if  opt.showimage  ==1          
        subplot(2,4,3);   
        plot(cr);
        %plot(fitStart:fitEnd,cr(fitStart:fitEnd),'or',plotx,ploty,'.g');		
        xlabel('z','Fontsize',16);		            
        ylabel('ZCorr','fontsize',16);		                            
        title('ZCorr','fontsize',10)		                            
        grid on
        
        subplot(2,4,4);                
        imshow(calProfile_,[]);		                            		                            
        title('CalProfile','fontsize',10)		                            
        grid on
    end
   
end








