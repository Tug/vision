% normalization
function processed_im = process_im(im, g, c)
    
    processed_im = im-min(min(im));
    processed_im = processed_im./max(max(processed_im));

    processed_im =  1./(1 + exp(g*(c-processed_im)));  % Apply Sigmoid function
