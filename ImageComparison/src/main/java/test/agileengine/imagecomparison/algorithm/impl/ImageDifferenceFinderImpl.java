package test.agileengine.imagecomparison.algorithm.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import test.agileengine.imagecomparison.algorithm.ImageDifferenceFinder;
import test.agileengine.imagecomparison.model.Pixel;

@Service
public class ImageDifferenceFinderImpl implements ImageDifferenceFinder {

    private static final int DIFFERENCE_PERCENT = 10;

    @Override
    public BufferedImage compareImages(BufferedImage imageA, BufferedImage imageB) {

        List<Pixel> differentPixels = new ArrayList<Pixel>();

        for (int x = 0; x < imageA.getWidth(); x++) {
            for (int y = 0; y < imageA.getHeight(); y++) {
                if (arePixelsDifferent(imageA, imageB, x, y)) {
                    differentPixels.add(new Pixel(x, y));
                }
            }
        }

        drawRectangles(differentPixels, imageB);

        return imageB;
    }

    /**
     * Check if pixel from imageA and imageB are different.
     * 
     * @param imageA
     * @param imageB
     * @param x
     * @param y
     * @return
     */
    private boolean arePixelsDifferent(final BufferedImage imageA, final BufferedImage imageB, final int x, final int y) {

        int rgb1 = imageA.getRGB(x, y);
        int rgb2 = imageB.getRGB(x, y);

        float percent = 100f;

        if (Math.abs(rgb1) > Math.abs(rgb1)) {
            percent = (Math.abs(rgb1) * 100.0f) / Math.abs(rgb2);
        } else {
            percent = (Math.abs(rgb2) * 100.0f) / Math.abs(rgb1);
        }

        // We should only mark pixel as "different" if difference between them is more than 10%.
        return percent < 100 - DIFFERENCE_PERCENT;
    }

    /**
     * Draw a rectangle around each difference.
     * 
     * @param differentPixels
     * @param image2
     */
    private void drawRectangles(List<Pixel> differentPixels, BufferedImage imageB) {

        if (differentPixels.size() > 0) {

            // init first and last coordinates in rectangle.
            int min_x = differentPixels.get(0).x;
            int min_y = differentPixels.get(0).y;
            int max_x = min_x;
            int max_y = min_y;

            int i = 0;

            outerFor: for (i = 0; i < differentPixels.size(); i++) {
                Pixel pixel = differentPixels.get(i);

                // is pixel touching another pixel ?
                if (i > 0) {
                    if (!isPixelTouching(pixel.x, pixel.y, differentPixels)) {
                        // current pixel is not touching any near pixel. End of current rectangle, proceed with next one recursively.
                        drawRectangles(differentPixels.subList(i, differentPixels.size()), imageB);
                        break outerFor;
                    } else {
                        // calculate the end of the rectangle.
                        max_x = pixel.x > max_x ? pixel.x : max_x;
                        max_y = pixel.y > max_y ? pixel.y : max_y;
                    }
                }
            }

            // Draw rectangle.
            imageB.getGraphics().drawRect(min_x, min_y, (max_x - min_x), (max_y - min_y));
        }
    }

    private static boolean isPixelTouching(int x, int y, List<Pixel> differentPixels) {

        int distance = 1;

        //@formatter:off
        if (differentPixels.indexOf(new Pixel((x - distance), (y - distance))) > -1 || 
            differentPixels.indexOf(new Pixel(x - distance, y)) > -1 || 
            differentPixels.indexOf(new Pixel(x, y - distance)) > -1) 
        {
            return true;
        }
      //@formatter:on

        return false;
    }

}
