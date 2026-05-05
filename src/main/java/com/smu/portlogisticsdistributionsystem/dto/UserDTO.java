package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String phone;
    private String password;
    private Integer state;
    private String country;
}
