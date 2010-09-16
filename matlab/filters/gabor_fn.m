function gb=gabor_fn(sigma,lambda,theta,psi,gamma)
% - sigma is the variance of the Gaussian envelope
% - lambda represents the wavelength of the cosine factor
% - theta represents the orientation (in radians) of the normal to the parallel
% stripes of a Gabor function
% - psi is the phase offset
% - gamma is the spatial aspect ratio, and specifies the ellipticity of the support of the Gabor function.


if nargin<3 || isempty(theta)
  theta=0;
end
if nargin<4 || isempty(psi)
 psi = 0;
end
if nargin<5 || isempty(gamma)
 gamma = 1;
end


sigma_x = sigma;
sigma_y = sigma/gamma;
 
% Bounding box
nstds = 3;
xmax = max(abs(nstds*sigma_x*cos(theta)),abs(nstds*sigma_y*sin(theta)));
xmax = ceil(max(1,xmax));
ymax = max(abs(nstds*sigma_x*sin(theta)),abs(nstds*sigma_y*cos(theta)));
ymax = ceil(max(1,ymax));
xmin = -xmax; ymin = -ymax;
[x,y] = meshgrid(xmin:xmax,ymin:ymax);
 
% Rotation 
x_theta=x*cos(theta)+y*sin(theta);
y_theta=-x*sin(theta)+y*cos(theta);
 
gb=exp(-.5*(x_theta.^2/sigma_x^2+y_theta.^2/sigma_y^2)).*cos(2*pi/lambda*x_theta+psi);
