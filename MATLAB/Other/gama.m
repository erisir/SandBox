t = 0:0.001:100;
N = 3;
k = 33;
y = t.^(N-1).*exp(-k./t);
plot(t,y);hold on;
N = 2.9;
y1 = t.^(N-1).*exp(-k./t);
plot(t,y1);