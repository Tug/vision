function imout = threshold( imin, thresh1, thresh2 )

    im = rgb2gray(imin);
    imout = repmat(0, size(im, 1), size(im, 2));
    whitevals = find(im >= thresh1 & im <= thresh2);
    imout(whitevals) = 255;
    