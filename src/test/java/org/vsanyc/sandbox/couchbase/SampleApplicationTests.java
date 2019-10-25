package org.vsanyc.sandbox.couchbase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.vsanyc.sandbox.couchbase.repositories.BuildingRepository;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleApplicationTests {

	@Autowired
	protected BuildingRepository businessUnityRepository;

	@Before
	public void deleteAll(){
		businessUnityRepository.deleteAll();
	}

	@Test
	public void contextLoads() {
	}

}
