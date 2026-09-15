package com.github.gokid96.e_commerce.coupon.support;

import tools.jackson.databind.ObjectMapper;
import com.github.gokid96.e_commerce.coupon.application.CouponFacade;
import com.github.gokid96.e_commerce.coupon.interfaces.CouponController;
import com.github.gokid96.e_commerce.coupon.interfaces.CouponInternalController;
import com.github.gokid96.e_commerce.support.restdocs.RestDocsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {
        CouponController.class,
        CouponInternalController.class,
})
public abstract class ControllerTestSupport extends RestDocsSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected CouponFacade couponFacade;
}
