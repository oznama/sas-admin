package com.mexico.sas.admin.api.dto.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class InvoiceFindDto implements Serializable {

    private static final long serialVersionUID = 6662468293567833135L;

    private Long id;
    private String invoiceNum;
    private String issuedDate;
    private String paymentDate;
    private Integer percentage;
    private Long status;
    private String amount;
    private String tax;
    private String total;
    private Boolean active;
    private Boolean eliminate;

}
