package org.ject.support.domain.tempapply.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ject.support.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum TemporaryApplicationErrorCode implements ErrorCode {
    NOT_FOUND("TEMP_APPLICATION_NOT_FOUND", "임시 지원서가 존재하지 않습니다.");
    private final String code;
    private final String message;
}
