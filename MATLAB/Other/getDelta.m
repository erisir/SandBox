function [ ret ] = getDelta( data )
%get delta of the peak data from findpeek function in origin
%Detailed explanation goes here
if(min(size(data)) ==1)
    ret = data;
return;
end

data = data';
data = sortrows(data,1);
data = data';

datax = data(1,:);
data = data(2,:);

sz = max(size(data));             %error check
if mod(sz,2) == 1                  
    tip = 'error occur!'
    yes_or_no_string=input('break=','s');
    yes_or_no=str2num(yes_or_no_string);
    while yes_or_no ~=0
        s = size(find(datax<yes_or_no));
        rt = max(s);
        if min(s) ~= 0
            if mod(rt,2) == 1
                tip = ['error !!                  ' '< [' num2str(yes_or_no) ']']
            end
        end
        yes_or_no_string=input('break=','s');
        yes_or_no=str2num(yes_or_no_string);
    end
    return;
end                                %error check end

len = max(size(data));
delta = abs(data(1:2:(len-1)) - data(2:2:len));
ret = delta;
end

