package com.example.muscimanger.service.impl;


import com.example.muscimanger.dto.CommonContextDto;
import com.example.muscimanger.dto.UserDto;
import com.example.muscimanger.model.User;
import com.example.muscimanger.service.UserService;
import com.example.muscimanger.until.TFM;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Service("SecurityService")
public class SecurityServiceImpl {

    @Resource(name = "userService")
    private UserService userService;
    @Autowired
    private TokenServiceImpl tokenService;

    public CommonContextDto createUserContext(String account, String password, HttpServletRequest request) throws Exception {
        if (account==null || password==null)
            throw new Exception("账号密码不为空");
        password = TFM.md5(password);
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        User dto =  userService.selectByAccountAndPwd(user);
        checkAuth(dto);
        HttpSession session = request.getSession();
        String userAgent = request.getHeader("user-agent");
        String token = tokenService.generateToken(userAgent,account);
        tokenService.saveToken(dto.getId().toString(),token,session);
        UserDto dto1 = new UserDto();
        BeanUtils.copyProperties(dto,dto1);
        dto1.setToken(token);
        return saveContext(dto1);
    }

    private CommonContextDto saveContext(UserDto dto){
        CommonContextDto context = new CommonContextDto();
        context.setAccount(dto.getAccount());
        context.setName(dto.getName());
        context.setRoles(dto.getRoles());
        context.setToken(dto.getToken());
        return context;
    }

    private void checkAuth(User dto) throws Exception{
        // 判断是否有登陆权限
        if (dto == null)
            throw new Exception("用户名、密码不正确");
        //存储用户信息
    }

    public void deleteToken(String key){
        tokenService.removeToken(key);
    }
}
