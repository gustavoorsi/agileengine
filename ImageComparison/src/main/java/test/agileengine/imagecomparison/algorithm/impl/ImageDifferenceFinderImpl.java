package test.agileengine.imagecomparison.algorithm.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import test.agileengine.imagecomparison.algorithm.ImageDifferenceFinder;
import test.agileengine.imagecomparison.model.Pixel;

//@formatter:off
/**
 * 
 * This is the core class that check if 2 images are different or not. The logic goes as follow:
 * 
 * 1) iterate over x and y coordinates from both images and check if a pixel with same coordinates from imageA is different from pixel in imageB.
 * 2) The resulting list will be ordered by x axis, so we now sort by y axis. For example: [0,3; 0,5; 0,1; 1,6; 1,2] -> [0,1; 0,3; 0,5; 1,2; 1,6 ]
 * 3) Then we find the minimum pixel and maximum pixel [ x,y; x+n,y+n ] for each group of "different pixels" and draw a rectangle surrounding them. 
 * 
 * 
 * @author Gustavo Orsi
 *
 */
//@formatter:on
@Service
public class ImageDifferenceFinderImpl implements ImageDifferenceFinder {

    private static final int DIFFERENCE_PERCENT = 10;

    @Override
    public BufferedImage compareImages(BufferedImage imageA, BufferedImage imageB) {

        // TODO: we should probably do a sanity check. If both images are equal, no need to execute the rest.

        List<Pixel> differentPixels = new ArrayList<Pixel>();

        for (int x = 0; x < imageA.getWidth(); x++) {
            for (int y = 0; y < imageA.getHeight(); y++) {
                if (arePixelsDifferent(imageA, imageB, x, y)) {
                    differentPixels.add(new Pixel(x, y));
                }
            }
        }

        differentPixels = sortList(differentPixels);

        drawRectangles(differentPixels, imageB);

        return imageB;
    }

    /**
     * Check if pixels from imageA and imageB are different. It is consider different if the rgb value is at least 10% different.
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

        // 100f means they are totally equal.
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
     * Draw a rectangle around each differences.
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

            for (i = 0; i < differentPixels.size(); i++) {
                Pixel pixel = differentPixels.get(i);

                // is pixel touching another pixel ?
                if (i > 0) {
                    if (!isPixelTouching(pixel.x, pixel.y, differentPixels)) {
                        // current pixel is not touching any near pixel. End of current rectangle, proceed with next one recursively.
                        drawRectangles(differentPixels.subList(i, differentPixels.size()), imageB);
                        break;
                    } else {
                        // recalculate the start and end of the rectangle.
                        max_x = pixel.x > max_x ? pixel.x : max_x;
                        max_y = pixel.y > max_y ? pixel.y : max_y;
                        min_x = pixel.x < min_x ? pixel.x : min_x;
                        min_y = pixel.y < min_y ? pixel.y : min_y;
                    }
                }
            }

            // Draw rectangle.
            imageB.getGraphics().drawRect(min_x, min_y, (max_x - min_x), (max_y - min_y));
        }
    }

    /**
     * simplier method to check if a pixel has another "marked" pixel in the surrounding environment.
     */
    // private static boolean isPixelTouching(int x, int y, List<Pixel> differentPixels) {
    //
    // int distance = 1;
    //
//        //@formatter:off
//        if (differentPixels.indexOf(new Pixel((x - distance), (y - distance))) > -1 || 
//            differentPixels.indexOf(new Pixel(x - distance, y)) > -1 || 
//            differentPixels.indexOf(new Pixel(x, y - distance)) > -1) 
//        {
//            return true;
//        }
//      //@formatter:on
    //
    // return false;
    // }

    /**
     * Check if a pixel is next to another pixel in the list. If they are "touching" it means we are still iterating over the same figure.
     * 
     * @param x
     * @param y
     * @param differentPixels
     *            The list of all pixels that are different from ImageA and ImageB
     * @return true if the pixel with coordinates x and y is sitting next to another "marked" pixel.
     */
    private boolean isPixelTouching(int x, int y, List<Pixel> differentPixels) {

        int currentPixelIndex = differentPixels.indexOf(new Pixel(x, y));

        for (int i = 0; i < currentPixelIndex; i++) {

            // check if there is a different Pixel surrounding the current one. This can be improved 100%.
            Pixel up = new Pixel(x, y - 1);
            Pixel up_right = new Pixel(x + 1, y - 1);
            Pixel up_left = new Pixel(x - 1, y - 1);
            Pixel right = new Pixel(x + 1, y);
            Pixel left = new Pixel(x - 1, y);
            Pixel down = new Pixel(x, y + 1);
            Pixel down_right = new Pixel(x + 1, y + 1);
            Pixel down_left = new Pixel(x - 1, y + 1);

            Pixel p = differentPixels.get(i);

            if (p.equals(up) || p.equals(up_right) || p.equals(up_left) || p.equals(right) || p.equals(left) || p.equals(down) || p.equals(down_right)
                    || p.equals(down_left)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Sort the list based on the "y" value and respect the order it has over the "x" value.For example: [0,3; 0,5; 0,1; 1,6; 1,2] -> [0,1; 0,3; 0,5; 1,2; 1,6 ]
     * 
     * @param unsortedPixelList
     * @return a sorted list.
     */
    private static List<Pixel> sortList(List<Pixel> unsortedPixelList) {

        Collections.sort(unsortedPixelList, new Comparator<Pixel>() {

            @Override
            public int compare(Pixel p1, Pixel p2) {

                if (p1.x <= p2.x && p1.y > p2.y) {
                    return -1;
                } else if (p1.x >= p2.x && p1.y < p2.y) {
                    return 1;
                } else {
                    return 0;
                }
            }

        });

        return unsortedPixelList;
    }

}
