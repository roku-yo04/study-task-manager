package com.mta.studytaskmanager.modules.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

//Subscription Plan - Subscription Plan (Gói thuê bao)
@Getter
@RequiredArgsConstructor
public enum PlanType {
    FREE(10, 5),
    PREMIUM(30, 10),  // Cao cấp
    ENTERPRISE(999,100); // Doanh nghiệp

    private final int maxTasks;
    private final int maxCategories;

}
