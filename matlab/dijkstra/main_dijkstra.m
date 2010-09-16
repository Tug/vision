% Read the ridge strength image from file.
RidgeStrength = imread('ridgeStrength.png');

% Compute the weight image for shorthest path computation.
RidgeStrength( RidgeStrength == 255 ) = 254;
WeightImg = 255 - double(RidgeStrength);

% Set the starting and terminal pixels.
XSource = 301;
YSource = 104;
XTerminal = 230;
YTerminal = 141;

% Determine the bounding box, in which the shorthest path computation will
% take place.
BBMinX = max( min( XSource, XTerminal ) - 10, 1);
BBMaxX = min( max( XSource, XTerminal ) + 10, size(OrigImg, 2));
BBMinY = max( min( YSource, YTerminal ) - 10, 1);
BBMaxY = min( max( YSource, YTerminal ) + 10, size(OrigImg, 1));
BBWeightImg = WeightImg(BBMinY:BBMaxY, BBMinX:BBMaxX);

% Compute the shorthest path between start and the terminal nodes.
[PathX PathY] = Dijkstra( XSource - BBMinX + 1, YSource - BBMinY + 1, ... 
                          XTerminal - BBMinX + 1, YTerminal - BBMinY + 1, ...
                          BBWeightImg );

% Correct the coordinates of the pixels that are on the shorthest path.
PathX = PathX + BBMinX - 1;
PathY = PathY + BBMinY - 1;

% Display the path overlaid on the original image.
figure;
imshow( uint8( OrigImg) );
title('Shortest path between the given points');
hold on;
plot(XSource, YSource, 'rd');
plot(XTerminal, YTerminal, 'gd');
plot(PathX, PathY, 'LineWidth', 2);
hold off;