function Statistic(data,swidth,bwidth)
%data origin data
%swidth smooth width of green line
%bwidth bar width
un = unique(data);
    len = size(un,1);
    temp = zeros(len);
    for i=1:len
        temp(i) = size(find( data ==un(i) ),1);
    end
   
    hold on
   
    minx = min(un);
    maxx = 32;%max(un);
    step = 0.01;
    swidth = swidth /step;
    x = minx:step:maxx;
    y=interp1(un, temp, x, 'spline');
    maxIndex=find(diff(sign(diff(y)))<0)+1;
    %maxIndex = maxIndex/100; 
    %plot(maxIndex,y(maxIndex),'*');
    y = y(:,1);
    bar(un,temp,bwidth);    
    plot(un,temp,'+r'); 
    plot(un,temp,'or'); 
    
    plot(x,y,'-g');
   
    set(gca,'XTick',[0:1:40]) ;
    set(gca,'YTick',[0:5:100]) ;
    
    ys = smooth(y,swidth);
    plot(x,ys,'.g');
    hold off;
    grid on
end