function [calProfile  pos] = GetCalibration(image_,roi_, opt_)
    
    %input prase
	opt.Radius            = opt_(1);
	opt.RInterstep        = opt_(2);
	opt.halfCorrWindow    = opt_(3);
	%opt.halfZTrackWindow  = opt_(4);  
	%opt.xFactor           = opt_(5);
	%opt.yFactor           = opt_(6);  
	opt.showimage         = opt_(7);
    %opt.method            = opt_(8);
    opt.imgwidth          = opt_(9);
    opt.imgheight         = opt_(10);
    
    roi.x       = roi_(1);
    roi.y       = roi_(2);
    roi.width   = roi_(3);
    roi.height  = roi_(4);
     
    %get ROI
	image = double(image_');
    % X & Y centers 
     
    pos = GosseCenter(image( roi.y:(roi.y+roi.height) , roi.x:(roi.x + roi.width)), opt);
    
    
    pos(1) = roi.x + pos(1);
    pos(2) = roi.y + pos(2);

    %get calibration curve    
    R = (0 : opt.RInterstep : opt.Radius)';   
    lenR = size(R, 1); 
    calProfile = zeros(1, lenR); 
    pf = cell(lenR, 1); 
  
    pf{1} = [0 0];
    for i = 2:lenR
        dTheta = 1 / abs(R(i));
        Theta  = (0 : dTheta : 2*pi)';
        pf{i}  = [R(i)*cos(Theta) R(i)*sin(Theta)];
    end
        
    for j = 1:lenR
        height  = opt.imgheight;
        profSum = zeros(1,size(pf{j},2));
        x  = pf{j}(:,1) + pos(1);
        y  = pf{j}(:,2) + pos(2);
        x0 = floor(x);
        x1 = x0 + 1;
        y0 = floor(y);
        y1 = y0 + 1;
        dx = x - x0;
        dy = y - y0;
        profSum = (1-dx).*(1-dy).*image(y0+ (x0-1)*height) + dx.*(1-dy).*image(y0+(x1-1)*height) + (1-dx).*dy.*image(y1+(x0-1)*height) + dx.*dy.*image(y1+ (x1-1)*height);
        calProfile(1, j) = sum(profSum, 1) / size(pf{j}, 1);       
    end  
    calProfile =  zscore(calProfile(1, :));  

    %show it if flag == 1:raw data track mode
    if opt.showimage  ==1 	
         subplot(2,4,7);
         plot(calProfile);		
         xlabel('zi','Fontsize',16);		            
         ylabel('ZiProfile','fontsize',16);		            
         title('ZiProfile','fontsize',10)		           
         grid on   
    end
end

