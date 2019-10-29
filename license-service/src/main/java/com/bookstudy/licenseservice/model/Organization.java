package com.bookstudy.licenseservice.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Organization implements Serializable {

    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;

}
