package com.shopr.catalog.service.user;

import com.shopr.catalog.model.User;
import com.shopr.catalog.request.LoginRequest;

public interface IUserService {
    User login(LoginRequest request);
}
