im1 = imread('street1.gif');
im2 = imread('street2.gif');


diff = uint8(double(im1) - double(im2));

%imagesc(diff)
imwrite(diff, 'diff.png');

%figure
%imagesc(imsubtract(im1, im2))

r = process_im(double(im1), 20, 0.5);

imshow(r)

wdg = imread('wdg.png');

r = threshold(wdg, 20, 150);

imshow(r)
