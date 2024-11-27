package org.badminton.api.aws.s3.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import org.badminton.api.aws.s3.model.dto.ImageUploadRequest;
import org.badminton.api.common.exception.EmptyFileException;
import org.badminton.api.common.exception.FileSizeOverException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractFileUploadService implements ImageService {
	private static final long MAX_FILE_SIZE = 2548576; // 1.5MB

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 s3Client;
	private final ImageConversionService imageConversionService;
	private static final String S3_URL_PREFIX = "https://badminton-team.s3.ap-northeast-2.amazonaws.com";
	private static final String CLOUDFRONT_URL_PREFIX = "https://d36om9pjoifd2y.cloudfront.net";

	public String uploadFile(ImageUploadRequest file) {
		MultipartFile uploadFile = file.multipartFile();

		try {
			if (uploadFile.getSize() > MAX_FILE_SIZE) {
				throw new FileSizeOverException();
			}

			// 파일이 비어있거나 파일 이름이 없는 경우 체크
			if (uploadFile.isEmpty() || Objects.isNull(uploadFile.getOriginalFilename())) {
				throw new EmptyFileException();
			}

			// 파일 형식이 WebP인지 체크
			byte[] fileBytes = uploadFile.getBytes();
			String fileExtension = getFileExtension(uploadFile.getOriginalFilename());

			byte[] processedImage;
			String newFileExtension;

			if ("webp".equalsIgnoreCase(fileExtension)) {
				processedImage = fileBytes;
				newFileExtension = "webp";
			} else if ("avif".equalsIgnoreCase(fileExtension)) {
				processedImage = fileBytes;
				newFileExtension = "avif";
			} else {
				processedImage = imageConversionService.convertToWebP(uploadFile);
				newFileExtension = "webp";
			}

			String fileName = makeFileName(newFileExtension);

			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(processedImage.length);
			objectMetadata.setContentType("image/webp");

			// S3에 파일 업로드
			s3Client.putObject(new PutObjectRequest(bucket, fileName,
				new ByteArrayInputStream(processedImage), objectMetadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));

			// 업로드 후 CloudFront URL 반환
			return toCloudFrontUrl(s3Client.getUrl(bucket, fileName).toString());

		} catch (IOException e) {
			throw new EmptyFileException();
		}
	}

	private String getFileExtension(String filename) {
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex == -1) {
			return "";
		}
		return filename.substring(dotIndex + 1);
	}

	private String toCloudFrontUrl(String originUrl) {
		if (originUrl != null && originUrl.startsWith(S3_URL_PREFIX)) {
			return originUrl.replace(S3_URL_PREFIX, CLOUDFRONT_URL_PREFIX);
		} else {
			throw new EmptyFileException();
		}
	}

	@Override
	public abstract String makeFileName(String newFileExtension);
}
