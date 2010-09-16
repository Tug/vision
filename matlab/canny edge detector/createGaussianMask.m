function [ mask ] = createGaussianMask( sigma )

    MIN_RATIO = 0.01;
    s = 16;
    G = exp(-(-s:s).^2/(2*sigma^2))/(sqrt(2*pi)*sigma);
    j=1;
    for i=1:2*s+1
        if G(i) > MIN_RATIO
            mask(j) = G(i);
            j = j+1;
        end
    end
end