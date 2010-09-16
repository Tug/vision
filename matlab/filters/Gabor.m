function [ out ] = Gabor( in )
    ker = gabor_fn(2, 0.1, 0.5, 1, 5)
    out = conv2(in, ker);
end