function [ TrajX, TrajY ] = Dijkstra( XSource, YSource, ...
                                       XTerminal, YTerminal, ... 
                                       WeightImage )
% Inputs: 
% 
% XSource:      The x coordinate of the starting pixel.
% YSource:      The y coordinate of the starting pixel.
% XTerminal:    The x coordinate of the destination pixel.
% YTerminal:    The y coordinate of the destination pixel.
% WeightImage:  2D matrix that contains the weights for each pixel in 
%               the image.
%
% 
%
% Outputs: 
% 
% TrajX:        The x coordinates of the shorthest path.
% TrajY:        The y coordinates of the shorthest path. A pair 
%               (TrajX(i), TrajY(i)) correponds to a pixel on the 
%               shortest path.
%

%% Create global variables
   global Q;
   global Dist;
                     
%% Exercise 2, Section 1 - Initializations:  DO NOT EDIT THIS SECTION!

   Height = size(WeightImage, 1);
   Width = size(WeightImage, 2);
                          
   % 2D matrix that contains distances from the source pixel to each 
   % pixel (i.e., node)
   Dist = inf( size(WeightImage) );

   % 2D matrix, each element of which corresponds to previous node in the 
   % optimal path from the source pixel. We use the 1D array index of a 
   % pixel as its unique node ID, so this matrix is composed of 1D array 
   % indices.
   Prev = zeros( size(WeightImage) );
   
   % Distance from the source to the source.
   Dist(YSource, XSource) = WeightImage(YSource, XSource);

   % Previous pixel ID of the source pixel is set to 0, meaning that there 
   % doesn't exist a parent of the source pixel.
   Prev(YSource, XSource) = 0;

   % Q is a matrix of unvisited elements.
   % false means that the element has been visited
   Q = true( size(WeightImage) );
   % Do not process the borders of the image
   Q(1, :) = false;
   Q(end, :) = false;
   Q(:, 1) = false;
   Q(:, end) = false;

   
%% Exercise 2, Section 2 - Implement the main loop of Dijkstra's algorithm.
   while( nnz( Q ) ~= 0 )
       
        u = GetMinElem( );

        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        [uy, ux] = ind2sub(size(Q), u);
        distu = Dist(uy,ux);
        if(distu == inf)
            break; %All remaining pixels are inaccessible from the source
        end
        RemoveElem( u );
        if(ux == XTerminal && uy == YTerminal)
            %stopping the algorithm once the shortest path to the destination vertex has been determined
            break;
        end
        % loop on every pixels around u
        for x=-1:1
            for y=-1:1
                if(x ~= 0 || y ~= 0) % if v != u
                    vx = ux + x;
                    vy = uy + y;
                    distv = Dist(vy,vx);
                    % dist_between(u, v) = weigth of v
                    dist_between_uv = WeightImage(vy,vx);
                    alt = distu + dist_between_uv;
                    if alt < distv      % Relax (u,v,a)
                        % update a shorter distance
                        Dist(vy,vx) = alt;
                        % add u as the previous node of v
                        Prev(vy,vx) = sub2ind(size(Prev), uy, ux);
                    end
                end
            end
        end
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

   end
   
   % Traverse the previous array to extract the 2D coordinates of the 
   % pixels that are on the path.
   [ TrajX, TrajY ] =  BackTraverse(XSource, YSource, ...
                                    XTerminal, YTerminal, ...
                                    Height, Width, Prev );
end


%% DO NOT EDIT ANYTHING FROM THIS POINT ON!

function [ u ] = GetMinElem( )
% This function returns the vertex in the queue with the smallest distance
% If there doesn't exist such a vertex in the queue, an empty variable is returned. 

    global Q;
    global Dist;

    NonzeroQIndices = find( Q );
    
    if isempty( NonzeroQIndices )
        u = [];
    else
        [NULL Indx] = min( Dist( NonzeroQIndices ) );
        u = NonzeroQIndices( Indx(1) );
    end

end

function [ ] = RemoveElem( Val )

    global Q;

    Q( Val ) = false;

end