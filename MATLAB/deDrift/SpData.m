function [ ret ] = SpData( data )
yes_or_no=1;
s1 = subplot(2,1,1);hold on;
plot(data,'k');
while yes_or_no==1
    disp('---------------������ȡ���ݵ������յ�')
    [x,y,button] = ginput(1);
    x = floor(x);
    while button ~=3 %��1=�����2=�У�3=�ң�
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
    
    while button ==3 %��1=�����2=�У�3=�ң�
        subplot(2,1,2);
         
        subData = data(begin:x,1);
        plot(subData,'k');
        grid on;   
        [x,y,button] = ginput(1);
        x = floor(x);
    end
    
    yes_or_no_string=input('���Ƿ���Ҫ�ض����ݡ�����Ҫ��0������Ҫ��1��=','s');
    yes_or_no=str2num(yes_or_no_string);
end
ret = data;
end

