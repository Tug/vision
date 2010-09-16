in1 = rgb2gray(imread('in1.jpg'));
in2 = rgb2gray(imread('in2.jpg'));
in3 = rgb2gray(imread('in3.jpg'));
in1d = double(in1);
in2d = double(in2);
in3d = double(in3);

out1 = cannyedgedetector(in1d);
%out2 = cannyedgedetector(in2d);
%out3 = cannyedgedetector(in3d);


figure;
imshow(uint8(out1));
%figure;
%imshow(uint8(out2));
%figure;
%imshow(uint8(out3));