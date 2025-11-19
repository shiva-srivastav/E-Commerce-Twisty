package com.twisty.dto.addressDTO;

import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String fullName;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phoneNumber;
    private Boolean isDefault;
}
