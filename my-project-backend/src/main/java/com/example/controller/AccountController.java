package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVo;
import com.example.entity.vo.request.ModifyEmailVo;
import com.example.entity.vo.response.AccountDetailsVo;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/user")
public class AccountController {

    @Resource
    AccountService accountService;

    @Resource
    AccountDetailsService detailsService;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        Account account = accountService.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }

    @GetMapping("/details")
    public RestBean<AccountDetailsVo> details(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountDetails accountDetails = Optional
                .ofNullable(detailsService.findAccountDetailsById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(accountDetails.asViewObject(AccountDetailsVo.class));
    }

    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid DetailsSaveVo DetailsVo) {
        boolean success = detailsService.saveAccountDetails(id, DetailsVo);
        return success ? RestBean.success() : RestBean.failure(400, "此用户名已被其他用户注册，请重新更换");
    }

    @PostMapping("modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid ModifyEmailVo ModifyEmailVo) {
        String result = accountService.modifyEmail(id, ModifyEmailVo);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }
}
