package com.swna.server.user.dto;

import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;

public record UserRecordDto(
    Long id,
    String email,
    String name,
    Role role,
    String city,
    String street,
    String surburb,
    String phone,
    String mobile
) {
    /**
     * 서버의 User 엔티티(Embedded된 Address 및 ContactInfo 포함)를 받아 DTO로 변환하는 팩토리 메서드
     */
    public static UserRecordDto from(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserRecordDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole(),
            user.getAddress() != null ? user.getAddress().getCity() : null,
            user.getAddress() != null ? user.getAddress().getStreet() : null,
            user.getAddress() != null ? user.getAddress().getSurburb() : null,
            user.getContact() != null ? user.getContact().getPhone() : null,
            user.getContact() != null ? user.getContact().getMobile() : null
        );
    }
}