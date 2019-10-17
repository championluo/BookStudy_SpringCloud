package com.bookstudy.licenseservice.utils;

import org.springframework.util.Assert;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext  = new ThreadLocal<UserContext>();

    public static final UserContext getUserContext() {

        UserContext userContext = UserContextHolder.userContext.get();

        if (userContext == null) {
            UserContext userContext1 = createUserContext();
            UserContextHolder.userContext.set(userContext1);
        }

        return UserContextHolder.userContext.get();
    }

    public static final void setContext(UserContext context){
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        UserContextHolder.userContext.set(context);
    }

    public static final UserContext createUserContext(){
        return new UserContext();
    }
}
