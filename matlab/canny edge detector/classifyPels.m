function [ out ] = classifyPels( gradImg, thrHigh, thrLow)
    FOREGROUND = 255;
    CANDIDATE = 127;
    %BACKGROUND = 0;
    s = size(gradImg);
    out = zeros(s);
    out = out + FOREGROUND * (gradImg > thrHigh);
    out = out + CANDIDATE * (gradImg > thrLow && gradImg < thrHigh);
end

