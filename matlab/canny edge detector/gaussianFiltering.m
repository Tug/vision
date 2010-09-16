function [ out ] = gaussianFiltering( in, sigma )

    G = createGaussianMask(sigma);
    out = doFiltering2D(in, G, G');
    
end