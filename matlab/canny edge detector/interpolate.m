function [ Gxy ] = interpolate( in, u )

    ru = round(u);
    du = u - ru;
    rx = ru(1);
    ry = ru(2);
	p = du(1);
	q = du(2);
	dx = 1;
	dy = 1;

	if p < 0
		dx = -1;
		p = -p;
    end
	if q < 0
		dy = -1;
		q = -q;
    end
    insize = size(in);
    height = insize(1);
    width = insize(2);
	
    Gxy = 0;
    
    if ((rx >1) && (ry >1) && (rx < width) && (ry < height))
	Gxy = (1 - q)*(1 - p) * in(ry, rx) ...
          + (1 - p) *  q  * in(ry+dy, rx) ...
          + (1 - q) *  p  * in(ry, rx+dx) ...
          +    p    *  q  * in(ry+dy, rx+dx);
    end

end

