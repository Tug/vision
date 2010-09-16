I = imread('sqr1.png');
I2 = double(I(:,:,1)); %first channel
E = edge(I2,'canny');
%imagesc(E);
acc = LinearHoughAccum(E);
thresh = 10;
acctresh = (acc > thresh);
imagesc(acc);
%imshow(uint8(acc));

