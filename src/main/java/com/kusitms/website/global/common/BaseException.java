package com.kusitms.website.global.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private BaseExceptionStatus status;
}
