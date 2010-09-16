function [ out ] = AverageFilter( in )
   kernel1 = [ 1  1  1;
               1  1  1;
               1  1  1];
   [hk, vk] = get1DKernels(kernel1);
   out = doFiltering2D(in, hk, vk);
end