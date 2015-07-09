package test.agileengine.imagecomparison.algorithm;

import java.awt.image.BufferedImage;

public interface ImageDifferenceFinder {

    /**
     * compares any 2 BufferedImages and return a BufferedÍmaged with difference drawn.
     * @param imageA
     * @param imageB
     * @return A BufferedImage with difference drawn.
     */
    BufferedImage compareImages(final BufferedImage imageA, BufferedImage imageB);

}
