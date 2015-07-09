package test.agileengine.imagecomparison;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import test.agileengine.imagecomparison.algorithm.ImageDifferenceFinder;

@SpringBootApplication
public class ImageComparisonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageComparisonApplication.class, args);
    }

    @Bean
    CommandLineRunner init(final ImageDifferenceFinder imageDifferenceFinder) {
        
        return (evt) -> {
            
            BufferedImage imageA = ImageIO.read(new File("c:\\house.png"));
            BufferedImage imageB = ImageIO.read(new File("c:\\house2.png"));
            
            BufferedImage imageDifference = imageDifferenceFinder.compareImages(imageA, imageB);
            
            File outputImage = new File("c:\\imageX.png");
            ImageIO.write(imageDifference, "png", outputImage);
            
        };

    }

}
