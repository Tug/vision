I = imread('Galere.jpg');
Id1 = double(I);
out = Prewitt(Id1);
figure;
imshow(uint8(out));
title('Prewitt');

I = imread('brain.jpg');
Id2 = double(I);
out = Prewitt(Id2);
figure;
imshow(uint8(out));
title('Prewitt');

out = Sobel(Id1);
figure;
imshow(uint8(out));
title('Sobel');

out = Sobel(Id2);
figure;
imshow(uint8(out));
title('Sobel');

out = Gabor(Id1);
figure;
imshow(uint8(out));
title('Gabor');

out = Gabor(Id2);
figure;
imshow(uint8(out));
title('Gabor');