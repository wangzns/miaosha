package com.sky.miaosha.controller;

import com.sky.miaosha.service.PromoService;
import com.sky.miaosha.vo.global.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : wang zns
 * @date : 2020-11-04
 */
@RestController
@RequestMapping("/promo")
public class PromoController {

    @Autowired
    private PromoService promoService;

    @RequestMapping("/publishPromo")
    public ResponseVO publishPromo(@RequestParam("promoId") String promoId) {
        promoService.publishPromo(Integer.valueOf(promoId));
        return ResponseVO.success();
    }

}
