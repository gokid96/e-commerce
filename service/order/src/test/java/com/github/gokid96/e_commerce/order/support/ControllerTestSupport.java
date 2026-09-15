package com.github.gokid96.e_commerce.order.support;

import tools.jackson.databind.ObjectMapper;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.order.interfaces.OrderController;
import com.github.gokid96.e_commerce.support.restdocs.RestDocsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {
        OrderController.class,
})
public abstract class ControllerTestSupport extends RestDocsSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected OrderService orderService;
}
