function testForce()
a  = 0.1:0.0001:1;
y = zeros(1,size(a,2));

j = 1;
for i  = 0.1:0.0001:1
    y(j) = myForce(i);
    j = j+1;
end

min(y)
plot(a,y,'.r',a,100*ones(1,size(a,2)),'.g');
end