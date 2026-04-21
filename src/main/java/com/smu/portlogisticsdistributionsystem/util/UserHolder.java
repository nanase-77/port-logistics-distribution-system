package com.smu.portlogisticsdistributionsystem.util;


import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;

public class UserHolder {
    private static final ThreadLocal<UserLoginDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserLoginDTO user){
        tl.set(user);
    }

    public static UserLoginDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
