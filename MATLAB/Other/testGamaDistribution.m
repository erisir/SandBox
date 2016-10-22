close all;
t = 0.01:0.01:3.5;
for k = 18:2:20;
N = 3;

y = t.^(N).*exp(-k.*t);
plot(t,y);grid on;hold on;
end 