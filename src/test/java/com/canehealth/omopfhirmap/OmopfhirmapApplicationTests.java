package com.canehealth.omopfhirmap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
class OmopfhirmapApplicationTests {

	@Test
	void contextLoads() {
		int randomNum = ThreadLocalRandom.current().nextInt(2, 5 + 1);
		randomNum = 2;
		//String[] args = {"tofhirbundle", String.valueOf(randomNum), "test-bundle.json"};
		String[] args = {"toomop", "test-bundle.json", String.valueOf(randomNum)};
		OmopfhirmapApplication.main(args);
	}

}
