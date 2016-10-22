function [  ] = sequenceGenarator( startNum,endNum )
%SEQUENCEGENARATOR Summary of this function goes here
%   Detailed explanation goes here
    path = 'D:\\Program Files\\smCamera\\Common\\FrameSequence\\';
    sequence = zeros(endNum,1);
    sequence(startNum:endNum,1) = 1;
    dlmwrite([path  int2str(startNum) '-' int2str(endNum) '.txt'], sequence,'newline', 'pc');
end

