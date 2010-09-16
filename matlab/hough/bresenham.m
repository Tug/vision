function [X,Y] = bresenham(x0, y0, orientation, width, height)

% Compute the parameters of a line
a = tan(orientation);
b = y0 - a * x0;

if abs(a) <= eps('double')
	x(1) = 1;
	y(1) = b;
	x(2) = width;
	y(2) = b;
elseif abs(1/a) <= eps('double')
	x(1) = x0;
	y(1) = 1;
	x(2) = x0;
	y(2) = height;
else
	np = 1;
	% left image border
	temp_y = a + b;
	if (temp_y >= 1) && (temp_y <= height)
		x(np) = 1;
		y(np) = temp_y;
		np = np + 1;
	end

	% right image boder
	temp_y = a * width + b;
	if (temp_y >= 1) && (temp_y <= height)
		x(np) = width;
		y(np) = temp_y;
		np = np + 1;
	end

	if np < 3
		% top image boder
		temp_x = (1 - b) / a;
		if (temp_x >= 1) && (temp_x <= width)
			x(np) = temp_x;
			y(np) = 1;
			np = np + 1;
		end
	end

	if np < 3
		% bottom image boder
		temp_x = (height - b) / a;
		if (temp_x >= 1) && (temp_x <= width)
			x(np) = temp_x;
			y(np) = height;
			np = np + 1;
		end
	end
end

x = round(x);
y = round(y);
steep = (abs(y(2)-y(1)) > abs(x(2)-x(1)));

if steep, [x,y] = swap(x,y); end

if x(1)>x(2), 
    [x(1),x(2)] = swap(x(1),x(2));
    [y(1),y(2)] = swap(y(1),y(2));
end

delx = x(2)-x(1);
dely = abs(y(2)-y(1));
error = 0;
x_n = x(1);
y_n = y(1);
if y(1) < y(2), ystep = 1; else ystep = -1; end 
for n = 1:delx+1
    if steep,
        X(n) = x_n;
        Y(n) = y_n;
    else
        X(n) = y_n;
        Y(n) = x_n;
    end    
    x_n = x_n + 1;
    error = error + dely;
    if bitshift(error,1) >= delx, % same as -> if 2*error >= delx, 
        y_n = y_n + ystep;
        error = error - delx;
    end    
end

[X Y] = swap(X,Y);

% -> a(y,x)
% plot(X, Y)
% axis([1 width 1 height]);

function [q,r] = swap(s,t)
% function SWAP
q = t; r = s;
