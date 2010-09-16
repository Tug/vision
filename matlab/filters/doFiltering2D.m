function [ out ] = doFiltering2D(in, ms_h, ms_v)

    out = doFiltering1D(in, ms_h);
    out = doFiltering1D(out, ms_v);
end