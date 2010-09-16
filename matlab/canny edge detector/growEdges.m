function [ classifImg ] = growEdges( classifImg )
    sizein = size(classifImg);
    width = sizein(2);
    height = sizein(1);
    out = in;
    for i=1:height
        for j=1:width
            if in(i,j) == FOREGROUND
                growEdges( classifImg )
                growEdges( classifImg )
                growEdges( classifImg )
                growEdges( classifImg )
            end
        end
    end
end

