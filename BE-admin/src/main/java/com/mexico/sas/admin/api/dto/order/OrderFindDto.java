package com.mexico.sas.admin.api.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrderFindDto implements Serializable {

    private static final long serialVersionUID = 7487435623797834423L;

    private Long id;
    private String orderNum;
    private String orderDate;
    private Long status;
    private String amount;
    private String tax;
    private String total;
    private Boolean active;
    private Boolean eliminate;

}
