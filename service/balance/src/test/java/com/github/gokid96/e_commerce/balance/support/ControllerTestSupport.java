package com.github.gokid96.e_commerce.balance.support;

import tools.jackson.databind.ObjectMapper;
import com.github.gokid96.e_commerce.balance.application.BalanceFacade;
import com.github.gokid96.e_commerce.balance.interfaces.BalanceController;
import com.github.gokid96.e_commerce.support.restdocs.RestDocsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {
        BalanceController.class,
})
public abstract class ControllerTestSupport extends RestDocsSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected BalanceFacade balanceFacade;
}
