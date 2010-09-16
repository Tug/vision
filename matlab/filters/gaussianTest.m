I = imread('Galere.jpg');
Id = double(I);
out = gaussianFiltering(Id,5);

figure;
imshow(uint8(out));

I = imread('brain.jpg');
Id = double(I);
out = gaussianFiltering(Id,5);

figure;
imshow(uint8(out));