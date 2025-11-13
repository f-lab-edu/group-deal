package com.app.groupdeal.domain.common;

import com.app.groupdeal.infrastructure.common.BaseEntity;
import lombok.Getter;

@Getter
public class BaseDomain extends BaseTime {

    protected String createdBy;
    protected String updatedBy;
}
