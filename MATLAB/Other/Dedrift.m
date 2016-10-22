function [ret  ] = Dedrift( filename ,delta,dl)
%DEDRIFT Summary of this function goes here
%   Detailed explanation goes here
    close all;
    data = dlmread(filename);
    subplot(2,1,1);hold on;plot(data);
    len = max(size(data));
    %dl = 10;
    for i = dl:(len-dl)
        if(data(i+1) - data(i) >delta)
            high = mean(data(i:i+dl));
            low = mean(data(i-dl:i));
            dt = high - low;
            
            plot(i+1,high,'r*' );
            plot(i,low,'r*' );
            data((i+1):len) = data((i+1):len) - dt;
        end
    end
    hold off;
    ret =data;
    subplot(2,1,2);plot(ret);
    dlmwrite([filename '.ok'],ret);
end

