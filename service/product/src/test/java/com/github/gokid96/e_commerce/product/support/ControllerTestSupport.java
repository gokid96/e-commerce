package com.github.gokid96.e_commerce.product.support;

import com.github.gokid96.e_commerce.product.application.ProductFacade;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.interfaces.ApiControllerAdvice;
import com.github.gokid96.e_commerce.product.interfaces.ProductController;
import com.github.gokid96.e_commerce.product.rank.application.RankFacade;
import com.github.gokid96.e_commerce.product.rank.interfaces.RankController;
import com.github.gokid96.e_commerce.support.restdocs.RestDocsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {
        ProductController.class,
        RankController.class,
        ApiControllerAdvice.class
})
public abstract class ControllerTestSupport extends RestDocsSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected ProductFacade productFacade;

    @MockitoBean
    protected RankFacade rankFacade;
}
