% Read the original image and its mask.
OrigImg = double( imread('retinal.png') );
Mask = double( logical( imread('mask.png') ) );
GaussianSTD = 2;

% Compute the ridge strength image.
RidgeStrength = ComputeRidgeStrength(OrigImg, Mask, GaussianSTD);
RidgeStrength = uint8(RidgeStrength);

% Display the result and write it to a file.
figure; 
imshow(RidgeStrength);
title('Ridge Strength Image');
imwrite(RidgeStrength, 'ridgeStrength.png');