function [ R ] = ComputeRidgeStrength( FilamImg, Mask, GaussSigma )

% Inputs: 
% 
% FilamImg:     The original image that contains filaments.
% Mask:			Binary mask image that is used to mask out those regions witout filaments.
% GaussSigma:	Standard deviation of the Gaussian that is used to smooth the image.
% 
%
% Outputs: 
% 
% R:			Ridge strength image. Pixel values must be in [0 255].
%
%
 
%% Exercise 1 - Implement the described in the text.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%a) Smooth the input image
gaussImg = gaussianFiltering( FilamImg, GaussSigma );

%b) Compute the second-order image derivatives
kernel1= [-1  0  1;
          -2  0  2;
          -1  0  1];
% fisrt derivative along x
[hk, vk] = get1DKernels(kernel1);
Gx = doFiltering2D(gaussImg, hk, vk);
% second derivative along x
Ixx = doFiltering2D(Gx, hk, vk);

kernel2= [ 1  2  1;
           0  0  0;
          -1 -2 -1];
% fisrt derivative along y
[hk, vk] = get1DKernels(kernel2);
Gy = doFiltering2D(gaussImg, hk, vk);
% second derivative along x
Iyy = doFiltering2D(Gy, hk, vk);

% combined derivative
Ixy = doFiltering2D(Gx, hk, vk);

%H = [ Ixx Ixy; Ixy Iyy ];

%c) Compute the ridge strength image
R = abs( 0.5 * ( Ixx + Iyy - sqrt( (Ixx - Iyy).^2 + 4*Ixy.^2 ) ) );

%d) Mask the ridge strength image by the inputted mask
R = R .* (Mask ~= 0);

%e) Normalize the values of the resulting ridge strength image between [0
%255]
R = R - min(min(R));
R = 255*(R./max(max(R)));

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
end