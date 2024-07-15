package org.somuga.aspect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Error {
    private String message;
    private String path;
    private int status;
    private String method;
    private Date timestamp;
}
