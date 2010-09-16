function [ accu ] = LinearHoughAccum( im )
    sim = size(im);
    h = sim(1);
    w = sim(2);
    diag = uint8(sqrt(h*h + w*w));
    diag2 = diag/2;
    center = [h/2 w/2];
    step = round(diag/10);
    accu = zeros(diag2+2, round(2*pi*step));
    for y=1:h,
        for x=1:w,
            p = im(y, x);
            if(p ~= 0),
                py = center(1) - y;
                px = center(2) - x;
                for theta=1:2*pi,
                    r = round(abs(px*cos(theta)+py*sin(theta)))+1;
                    t = round(theta*step);
                    accu(r, t) = accu(r, t) + 1;
                end
            end
        end
    end
end

