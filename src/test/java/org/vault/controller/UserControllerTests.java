package org.vault.controller;

import java.util.ArrayList;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.vault.model.User;
import org.vault.repo.TopicGroupRepo;
import org.vault.repo.TopicRepo;
import org.vault.repo.UserRepo;





@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
//@SpringBootTest
public class UserControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean	
	private UserRepo userrepo;
	
	@MockBean
	private TopicGroupRepo topicGroupRepo; 

	@MockBean
	private TopicRepo topicRepo;
	
//	@MockBean
//	@Qualifier("userValidator")
//	private Validator validator;
	
	@Test
	public void testCommonDashboardNavig() throws Exception {
		System.out.println("in test...");
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.view().name("commonDashboard.html"));
	}
	
	@Test
	public void testAdminUserDisplay() throws Exception {
		System.out.println("setup start...");
		List<User> userList = new ArrayList<User>();
		User user = new User();
		user.setId(1);
		user.setFirstname("Sridhar");
		user.setLastname("Manohar");
		userList.add(user);
		org.mockito.Mockito.when(this.userrepo.findAll())
						   .thenReturn(userList);
		System.out.println("setup complete...");

		System.out.println("test start");
		mockMvc.perform(MockMvcRequestBuilders.get("/adminDisplayUsers"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.model().attributeExists("allusers"))
		.andExpect(MockMvcResultMatchers.view().name("adminDisplayUsers.html"));
		System.out.println("test complete");
	}

	@Test
	public void testAdminUserDisplayNullList() throws Exception {
		System.out.println("setup start...");
		List<User> userList = new ArrayList<User>();
		User user0 = new User();
		userList.add(user0);

		org.mockito.Mockito.when(this.userrepo.findAll())
						   .thenReturn(userList);
		System.out.println("setup complete...");

		System.out.println("test start");
		mockMvc.perform(MockMvcRequestBuilders.get("/adminDisplayUsers"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.model().attributeExists("allusers"))
		.andExpect(MockMvcResultMatchers.view().name("adminDisplayUsers.html"));
		System.out.println("test complete");
	}
	
	@Test
	public void testEditUserPositiveCase() throws Exception {
		User user0 = new User();
		user0.setId(1);
		user0.setFirstname("fname");
		user0.setLastname("lname");
		org.mockito.Mockito.when(this.userrepo.findById(1))
							.thenReturn(user0);

		mockMvc.perform(MockMvcRequestBuilders.get("/editUser/{userId}",1))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.model().attributeExists("user"))
		.andExpect(MockMvcResultMatchers.model().attribute("user", org.hamcrest.Matchers.hasProperty("id", org.hamcrest.Matchers.is(1))))
		.andExpect(MockMvcResultMatchers.model().attribute("user", org.hamcrest.Matchers.hasProperty("firstname", org.hamcrest.Matchers.is("fname"))))
		.andExpect(MockMvcResultMatchers.model().attribute("user", org.hamcrest.Matchers.hasProperty("lastname", org.hamcrest.Matchers.is("lname"))))
		.andExpect(MockMvcResultMatchers.view().name("editUser.html"));
		}
	

	@Test
	//when no matching user found case.
	public void testEditUserNegativeCase() throws Exception {
		User user0 = null;
		org.mockito.Mockito.when(this.userrepo.findById(1))
							.thenReturn(user0);

		mockMvc.perform(MockMvcRequestBuilders.get("/editUser/{userId}",1))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.model().attributeExists("user"))
		.andExpect(MockMvcResultMatchers.view().name("editUser.html"));
	}
 }
