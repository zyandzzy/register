package com.example.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVo;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {

    @Resource
    AccountService service;

    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public synchronized boolean saveAccountDetails(int id, DetailsSaveVo detailsSaveVo) {
        Account account = service.findAccountByNameOrEmail(detailsSaveVo.getUsername());
        if (account == null || account.getId() == id) {
            if(service.update()
                    .eq("id", id)
                    .set("username", detailsSaveVo.getUsername())
                    .update()) {
                this.saveOrUpdate(new AccountDetails(
                        id, detailsSaveVo.getGender(), detailsSaveVo.getPhone(),
                        detailsSaveVo.getQq(), detailsSaveVo.getWx(), detailsSaveVo.getDesc()
                ));
                return true;
            }
        }
        return false;
    }
}
