function [ZPosum_dedrift ] = de_drift(ZPos)
close all;

ZPos(find(isnan(ZPos)==1)) = ZPos(100);
fit_x=(1:size(ZPos,1))';
fit_y=ZPos;

[a,b]=fit(fit_x,fit_y,'poly1');
p1=a.p1;
p2=a.p2;
fit_y2=p1.*fit_x+p2;
delta=fit_y2-fit_y2(1);
fit_y3=fit_y-0;
figure('name','all through poly fit')
subplot(2,1,1);
plot(fit_x,fit_y);grid on;
subplot(2,1,2);
plot(fit_x,fit_y3);grid on;
disp('---------------需要处理单组数据吗？0-不需要；1-需要')
yes_or_no_string=input('judge=','s');
yes_or_no=str2num(yes_or_no_string);
fit_y3_origin=fit_y;
figure('name','one part poly fit');
subplot(2,1,1);
plot(fit_x,fit_y3_origin);grid on;
subplot(2,1,2);
plot(fit_x,fit_y3);grid on;
while yes_or_no==1
    disp('---------------请点击拟合数据的起点和终点')
    [step_data_x,step_data_y] = ginput(1);
    step_first_x=floor(step_data_x(1,1));
    step_end_x=floor(step_data_x(2,1));
    step_first_y=floor(step_data_y(1,1));
    step_end_y=floor(step_data_y(2,1));
    number=size(fit_y3,1);
    if step_end_x>number
        fit_x4=fit_x(step_first_x:number);
        fit_y4=fit_y3(step_first_x:number);
    elseif step_first_x<1
        fit_x4=fit_x(1:step_end_x);
        fit_y4=fit_y3(1:step_end_x);
    else   
        fit_x4=fit_x(step_first_x:step_end_x);
        fit_y4=fit_y3(step_first_x:step_end_x);
    end
    [a,b]=fit(fit_x4,fit_y4,'poly1');
    p1=a.p1;
    p2=a.p2;
    k = (step_end_y-step_first_y)/(step_end_x-step_first_x);
   % fit_y5=p1.*fit_x4+p2;
    fit_y5=k.*(fit_x4-step_first_x)+step_first_y;
    delta=fit_y5-fit_y5(1);
    fit_y6=fit_y4-delta;
    
    fit_y7=fit_y3;
    
    fit_y7(step_first_x:step_end_x)=fit_y6;
    
    fit_y7((step_end_x+1):number)=fit_y7((step_end_x+1):number)-delta(end);
    
    subplot(2,1,1);
    plot(fit_x,fit_y3_origin);grid on;
    subplot(2,1,2);
    plot(fit_x,fit_y7);grid on;
    
    fit_y3=fit_y7;
    
    disp('---------------还需要处理数据吗？0-不需要；1-需要')
    yes_or_no_string=input('judge=','s');
    yes_or_no=str2num(yes_or_no_string);
    
end
ZPosum_dedrift=fit_y3;
end
