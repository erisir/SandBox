a = imread('2.tif');
b =a;
b(find(a>13000))=255555;
imshow(b);