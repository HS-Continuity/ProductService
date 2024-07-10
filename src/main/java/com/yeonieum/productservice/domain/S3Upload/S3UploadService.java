package com.yeonieum.productservice.domain.S3Upload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

//@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    //@Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    /**
     * 이미지 업로드
     *
     * @param image
     * @return
     * @throws IOException
     */
    public String uploadImage(MultipartFile image) throws IOException {
        //[STEP1]이미지 검증
        if (image == null || image.isEmpty()) {
            return null;
        }
        validateImageFileExtention(image.getOriginalFilename());

        //[STEP2]고유한 파일 이름 생성 및 메타데이터 설정
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

        //[STEP3]메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();

        //[STEP4]파일의 크기 설정
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        //[STEP5]파일 S3에 업로드
        amazonS3.putObject(bucket, fileName, image.getInputStream(), metadata);

        //[STEP6]파일 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /**
     * 파일키로 이미지 url 반환
     * @param fileKey
     * @return
     */
    public String getImageUrlFromS3(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            System.out.println("No file key provided.");
            return null;
        }

        if (fileKey.startsWith("http")) {
            System.out.println("File key is already a URL: " + fileKey);
            return fileKey;
        }

        String url = amazonS3.getUrl(bucket, fileKey).toString();
        return url;
    }

    /**
     * S3에서 이미지 삭제하기
     * @param imageAddress
     */
    public void deleteImageFromS3(String imageAddress){
        String key = getKeyFromImageAddress(imageAddress);
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
    }


    /**
     * 이미지 주소로 부터 이미지 키 반환
     * @param imageAddress
     * @return
     */
    private String getKeyFromImageAddress(String imageAddress) {
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException | UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 파일 확장자 유효성 검증
     * @param filename
     */
    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            // 에러 던지기
            return;
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            // 에러 : 유효하지 않은 파일
        }
    }
}