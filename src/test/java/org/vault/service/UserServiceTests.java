package org.vault.service;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.vault.model.User;
import org.vault.repo.TopicGroupRepo;
import org.vault.repo.TopicRepo;
import org.vault.repo.UserRepo;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@AutoConfigureTestDatabase(replace=Replace.NONE)
//@WebMvcTest(UserController.class)
public class UserServiceTests {

//	@Autowired private MockMvc mockMvc;
	
	@Autowired private UserRepo userrepo;
	
	@Autowired private TopicGroupRepo topicGroupRepo; 

	@Autowired private TopicRepo topicRepo;
	
	
//	@Test
//	public void testCommonDashboardNavig() throws Exception {
//		System.out.println("in test...");
//		mockMvc.perform(MockMvcRequestBuilders.get("/"))
//		.andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.view().name("commonDashboard.html"));
//	}


	@Test
	@Transactional
	public void checkUser() {
		List<User> users = this.userrepo.findByFirstnameAndLastname("Sridhar", "Manohar");
		assertThat(users.isEmpty()).isFalse();
	}
	
	@Test
	@Transactional
	public void shouldAddUser() {
		List<User> users = this.userrepo.findByFirstnameAndLastname("King", "Kong");
		User user = new User();
		if(users.isEmpty()) {
			user.setFirstname("King");
			user.setLastname("Kong");
			this.userrepo.save(user);
			System.out.println("user added");
		}
		System.out.println("user id:"+ user.getId());
		assertThat(user.getId()).isNotEqualTo(0);
	}
}
