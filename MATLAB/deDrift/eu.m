function [ret] = eu(filename)
  close all;
split = ',';
skipRow = 1;
invert = 0;%1:invert 0:do nothing

data = getData(filename,split,skipRow,invert);%��txt �ж�ȡ��Ч�����У�ת����λΪnm������Ҫ��ת����ʹ�÷�Ӧ�峯��
 
data = SpData( data );
 return;
data =   de_drift(data);
data = data - min(data);
ret = data;

x = 1:size(data,1);
yes_or_no=2;
while yes_or_no ~=0
    
    p = fpeak(x,data,yes_or_no);
    close all;
    subplot(2,1,1); 
    hold on;
    plot(data,'k');
    plot(p(:,1),p(:,2),'ro');grid on;
    hold off
    maxs = max(p(:,2));
    minx = min(p(:,2));
    subplot(2,1,2); hist(p(:,2),(maxs - minx )/2);grid on;
    set(gca,'XTick',minx:maxs);
    
    yes_or_no_string=input('���Ƿ���Ҫ�޸ľ��ȡ� ����Ҫ��0������Ҫ������ֵ�� =','s');
    yes_or_no=str2num(yes_or_no_string);
end


end

