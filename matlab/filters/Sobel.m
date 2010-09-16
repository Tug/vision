function [ out ] = Sobel( in )

   kernel1 = [-1  0  1;
              -2  0  2;
              -1  0  1];
   kernel2 = [ 1  2  1;
               0  0  0;
              -1 -2 -1];
   [hk, vk] = get1DKernels(kernel1);
   Gx = doFiltering2D(in, hk, vk);
   [hk, vk] = get1DKernels(kernel2);
   Gy = doFiltering2D(in, hk, vk);
   out = sqrt(Gx.^2 + Gy.^2);
end