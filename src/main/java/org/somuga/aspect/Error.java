package org.somuga.aspect;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Error {
    private String message;
    private String path;
    private int status;
    private String method;
    private Date timestamp;
}
