package com.binarybrain.user;

import com.binarybrain.user.controller.UserControllerTest;
import com.binarybrain.user.service.CustomUserDetailsServiceTest;

import com.binarybrain.user.service.RefreshTokenServiceTest;
import com.binarybrain.user.service.UserServiceImplTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Suite
@SelectClasses({UserControllerTest.class, UserServiceImplTest.class, CustomUserDetailsServiceTest.class,
		RefreshTokenServiceTest.class})
class OnlineClassroomManagementSystemApplicationTests {

	@Test
	void contextLoads() {
		// This method is intentionally left empty.
		// It is used to verify that the Spring application context loads successfully.
	}

}
