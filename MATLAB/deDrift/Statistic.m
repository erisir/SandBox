function [ ret ] = Statistic(data,index)

Value = round(data(index));

uValue = unique(Value);

ret = 1:max(uValue);


for i=1:max(uValue)
    ret(i)=sum(find(Value==i));
end

end

