package com.test.permissionusesjwt.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailId implements Serializable {
    String order;   // Order.id
    String course;  // Course.id
}
