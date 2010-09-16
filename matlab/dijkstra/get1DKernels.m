function [ h_ker, v_ker ] = get1DKernels( in_ker )
    [L,U] = lu(in_ker);
    h_ker = U(1,:);
    v_ker = L(:,1);
end