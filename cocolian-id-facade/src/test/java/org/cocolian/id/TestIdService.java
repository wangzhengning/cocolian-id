/**
 * 
 */
package org.cocolian.id;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author admin
 *
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = IdServiceConfiguration.class, webEnvironment = WebEnvironment.NONE)
public class TestIdService {
	@Autowired
	private IdService idService;
	
	@Test
	public void testNextId(){
		Assert.assertTrue(idService.nextId()>0);
	}
}
