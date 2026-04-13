package com.github.gokid96.e_commerce.support;

import com.github.gokid96.e_commerce.balance.controller.BalanceController;
import com.github.gokid96.e_commerce.common.ApiControllerAdvice;
import com.github.gokid96.e_commerce.coupon.controller.CouponController;
import com.github.gokid96.e_commerce.order.controller.OrderController;
import com.github.gokid96.e_commerce.product.controller.ProductController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {
        BalanceController.class,
        CouponController.class,
        OrderController.class,
        ProductController.class,
        ApiControllerAdvice.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}