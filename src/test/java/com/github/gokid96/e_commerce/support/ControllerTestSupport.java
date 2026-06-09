package com.github.gokid96.e_commerce.support;

import com.github.gokid96.e_commerce.balance.application.BalanceFacade;
import com.github.gokid96.e_commerce.balance.interfaces.BalanceController;
import com.github.gokid96.e_commerce.common.ApiControllerAdvice;
import com.github.gokid96.e_commerce.coupon.application.CouponFacade;
import com.github.gokid96.e_commerce.coupon.interfaces.CouponController;
import com.github.gokid96.e_commerce.order.application.OrderFacade;
import com.github.gokid96.e_commerce.order.interfaces.OrderController;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.interfaces.ProductController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {
        BalanceController.class,
        CouponController.class,
        ProductController.class,
        OrderController.class,
        ApiControllerAdvice.class
})
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockitoBean
    protected BalanceFacade balanceFacade;
    @MockitoBean
    protected CouponFacade couponFacade;
    @MockitoBean
    protected ProductService productService;
    @MockitoBean
    protected OrderFacade orderFacade;
}