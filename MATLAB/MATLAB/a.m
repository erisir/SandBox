function [ len ] = a( ch )
%A Summary of this function goes here
%   Detailed explanation goes here
path = 'I:\trace1.smt';
 
for i=256:272
    fid=fopen(path,'r');

    [Num s]=fread(fid,i,'char');
    len = fread(fid,1,ch)
     fclose(fid);
end
return;
 data = dlmread('a.txt');
 fid=fopen('abin.smt','w');
 fwrite(fid,data(:,n),ch);
 fclose(fid);
 len = 0;
end

