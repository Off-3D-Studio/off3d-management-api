package com.off3d.studio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class Off3dStudioApplicationTests {

	@Test
	@DisplayName("Deve carregar o contexto do Spring com sucesso")
	void contextLoads() {
	}
}