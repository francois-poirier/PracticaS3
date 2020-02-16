package es.codeurjc.daw.bucket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonConfig {


	@Bean
	public AmazonS3 s3client() {

		AmazonS3 s3Client = AmazonS3ClientBuilder
	              .standard()
	              .withRegion(Regions.US_EAST_1)
	              .build();

		return s3Client;
	}
}
