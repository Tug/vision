function [ out ] = doFiltering1D( in, ms )

    in_size = size(in);
    ms_size = size(ms);
    out = zeros(in_size);
    
    if ms_size(1) == 1
        ms2 = floor(ms_size(2)/2);
        for i=1:in_size(1)
            for j=1+ms2:in_size(2)-ms2
                out(i,j) = in(i,j-ms2:j+ms2) * ms';
            end
        end
    else
        ms2 = floor(ms_size(1)/2);
        for i=1:in_size(2)
            for j=1+ms2:in_size(1)-ms2
                out(j,i) = in(j-ms2:j+ms2,i)' * ms;
            end
        end
    end
end