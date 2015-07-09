package test.agileengine.imagecomparison;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.agileengine.imagecomparison.algorithm.ImageDifferenceFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ImageComparisonApplication.class)
public class ImageComparisonApplicationTests {

    @Autowired
    private ImageDifferenceFinder imageDifferenceFinder;

    @Test
    public void test() throws IOException {

//        BufferedImage imageA = ImageIO.read(new File("c:\\house.png"));
//        BufferedImage imageB = ImageIO.read(new File("c:\\house4.png"));
//
//        BufferedImage imageDifference = imageDifferenceFinder.compareImages(imageA, imageB);
//
//        File outputImage = new File("c:\\imageX.png");
//        ImageIO.write(imageDifference, "png", outputImage);

    }

}
