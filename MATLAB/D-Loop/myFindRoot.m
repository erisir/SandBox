function [ index ] = myFindRoot(x1,y1,x2,y2 )
%MYFINDROOT Ѱ���������߽��㣬���ؽ���y1(x1) ��λ��
%   Detailed explanation goes here
    delta = 10000;
    index = 0;
    for i = 1:max(size(x1))
        deltai = 10000;
        for j = 1:max(size(x2))
            temp = (y1(i) - y2(j))^2 + (x1(i) - x2(j))^2;
            if temp <deltai
                deltai = temp;
            end
        end%for x2
    
        if deltai <delta
            delta = deltai;
            index = i;
        end
    end% for x1

end

