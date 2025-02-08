package com.onlineclassroom.management;

import com.onlineclassroom.management.controller.UserControllerTest;
import com.onlineclassroom.management.service.CustomUserDetailsServiceTest;
import com.onlineclassroom.management.service.UserServiceImplTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Suite
@SelectClasses({UserControllerTest.class, UserServiceImplTest.class, CustomUserDetailsServiceTest.class})
class OnlineClassroomManagementSystemApplicationTests {

	@Test
	void contextLoads() {
	}

}
