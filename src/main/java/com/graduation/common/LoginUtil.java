package com.graduation.common;


import com.graduation.user.UserDO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class LoginUtil {

    public static final Map<String, UserDO> LOGIN_MAP = new HashMap<>();

    public static UserDO loginer(HttpServletRequest request) {
        return LOGIN_MAP.get(request.getHeader("token"));
    }

}
