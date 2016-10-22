function [ ret ] = SpData( data )
yes_or_no=1;
s1 = subplot(2,1,1);hold on;
plot(data,'k');
while yes_or_no==1
    disp('---------------请点击截取数据的起点和终点')
    [x,y,button] = ginput(1);
    x = floor(x);
    while button ~=3 %（1=左键，2=中，3=右）
        cla(s1);
        s1 = subplot(2,1,1);hold on;
        plot(data,'k');
        plot(x,y,'*r');
        plot(x,y,'or');
        hold off;
        grid on;
        begin = x;
        [x,y,button] = ginput(1);
        x = floor(x);
    end
    
    while button ==3 %（1=左键，2=中，3=右）
        subplot(2,1,2);
         
        subData = data(begin:x,1);
        plot(subData,'k');
        grid on;   
        [x,y,button] = ginput(1);
        x = floor(x);
    end
    
    yes_or_no_string=input('【是否需要截断数据】不需要【0】；需要【1】=','s');
    yes_or_no=str2num(yes_or_no_string);
end
ret = data;
end

