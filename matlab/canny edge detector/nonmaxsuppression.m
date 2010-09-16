function [ out ] = nonmaxsuppression( in )
    u = [sqrt(2), sqrt(2)];
    sizein = size(in);
    width = sizein(2);
    height = sizein(1);
    out = in;
    for i=1:height
        for j=1:width
            i
            j
            GA = in(i,j)
            GA1 = interpolate(in, [j, i]+u)
            GA2 = interpolate(in, [j, i]-u)
            if GA < GA1 || GA < GA2
                out(i,j) = 0;
            end
        end
    end
end