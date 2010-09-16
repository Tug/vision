function [ out ] = cannyedgedetector(in)

out = in;

%1 smoothing
out = gaussianFiltering( out, 1 );

%2 Gradient
out = Sobel( out );

imshow(uint8(out));

%3 Non-Maximum Suppression
out2 = nonmaxsuppression( out );
max(max(out2 ~= out))