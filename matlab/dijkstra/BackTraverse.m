function [ TrajX, TrajY ] = BackTraverse( XSource, YSource, ...
                                          XTerminal, YTerminal, ... 
                                          Height, Width, ...
                                          Prev )
% Inputs: 
% 
% XSource:      The x coordinate of the starting pixel.
% YSource:      The y coordinate of the starting pixel.
% XTerminal:    The x coordinate of the destination pixel.
% YTerminal:    The y coordinate of the destination pixel.
% Height:		Number of rows of the original image.
% Height:		Number of columns of the original image.
% Prev:			2D matrix computed by Dijkstra's algorithm. Each element of 
%				this matrix corresponds to previous node in the 
%				optimal path from the source pixel. The value at a pixel 
%				is equal to the 1D array index of its parent pixel.
%
% Outputs: 
% 
% TrajX:        The x coordinates of the shorthest path.
% TrajY:        The y coordinates of the shorthest path. A pair 
%               (TrajX(i), TrajY(i)) correponds to a pixel on the path.

% Exercise 2, Section 3 - Back traverse the 'Prev' array from the terminal
% node to the source node and return the 2D coordinates of the pixels that 
% are on the shorthest path.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % start from the terminal node
    ux = XTerminal;
    uy = YTerminal;
    s = [YSource XSource];
    i = 1;
    % while not at the source
    while [uy ux] ~= s
        % add the node to P = [TrajY, TrajX]
        TrajX(i) = ux;
        TrajY(i) = uy;
        % get the previous node
        u = Prev(uy,ux);
        % convert it to 2D coordinates
        [uy, ux] = ind2sub(size(Prev), u);
        i = i + 1;
    end
    %add the source to the path
    TrajX(i) = XSource;
    TrajY(i) = YSource;
    %order is not important
    %TrajX = fliplr(TrajX);
    %TrajY = fliplr(TrajY);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

end