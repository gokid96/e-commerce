package com.github.gokid96.e_commerce.rank.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.rank.application.RankCriteria;
import com.github.gokid96.e_commerce.rank.application.RankFacade;
import com.github.gokid96.e_commerce.rank.application.RankResult;
import com.github.gokid96.e_commerce.rank.interfaces.dto.RankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class RankController {

    private final RankFacade rankFacade;

    @GetMapping("/ranks")
    public ApiResponse<RankResponse.PopularProducts> getPopularProducts() {
        RankResult.PopularProducts popularProducts =
                rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());
        return ApiResponse.ok(RankResponse.PopularProducts.of(popularProducts));
    }
}
