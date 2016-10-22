function [ ret ] = AutoHist(   )
%AUTOHIST Summary of this function goes here
%   Detailed explanation goes here
    data = getData();
    data(find(data>= 30)) = [];
    %data(find(data<= 0.2)) = [];
    del = max(data) - min(data);
    Rn = 0:0.51:4.08;
    Offset = 6.85;
    Rreal =  Offset-Rn;
    R0 = 6;
    FRET = 1./(1+(Rreal./R0).^(6));
    FRET = [3 6 9 12 15 18 21 24 27];
    for i= 20:0.2:32
        hist(data,i);
        title([ '[' num2str(i) '] ' 'min : ' num2str(max(data)) 'max : ' num2str(min(data)) ' binning :  ' num2str(del/i)]);
        set(gca,'XTick',FRET);
        grid on;
        pause;
    end
    
end

