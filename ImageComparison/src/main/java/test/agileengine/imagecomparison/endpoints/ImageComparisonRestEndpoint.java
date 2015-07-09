package test.agileengine.imagecomparison.endpoints;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import test.agileengine.imagecomparison.algorithm.ImageDifferenceFinder;

@RestController
@RequestMapping("/api/v1/image")
public class ImageComparisonRestEndpoint {

    private final ImageDifferenceFinder imageDifferenceFinder;

    @Autowired
    public ImageComparisonRestEndpoint(final ImageDifferenceFinder imageDifferenceFinder) {
        this.imageDifferenceFinder = imageDifferenceFinder;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<byte[]> upload(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {

        BufferedImage imageA = null;
        BufferedImage imageB = null;

        MultipartFile file1 = (MultipartFile) request.getFileMap().values().toArray()[0];
        MultipartFile file2 = (MultipartFile) request.getFileMap().values().toArray()[1];

        InputStream in = new ByteArrayInputStream(file1.getBytes());
        imageA = ImageIO.read(in);

        InputStream in2 = new ByteArrayInputStream(file2.getBytes());
        imageB = ImageIO.read(in2);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        BufferedImage imageDifference = imageDifferenceFinder.compareImages(imageA, imageB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageDifference, "png", baos);
        baos.flush();

        return new ResponseEntity<byte[]>(Base64Utils.encode(baos.toByteArray()), headers, HttpStatus.CREATED);

    }
}
