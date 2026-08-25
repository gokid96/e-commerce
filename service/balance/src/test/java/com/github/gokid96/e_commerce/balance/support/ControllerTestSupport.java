package com.github.gokid96.e_commerce.balance.support;

import tools.jackson.databind.ObjectMapper;
import com.github.gokid96.e_commerce.balance.application.BalanceFacade;
import com.github.gokid96.e_commerce.balance.interfaces.BalanceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        BalanceController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected BalanceFacade balanceFacade;
}
