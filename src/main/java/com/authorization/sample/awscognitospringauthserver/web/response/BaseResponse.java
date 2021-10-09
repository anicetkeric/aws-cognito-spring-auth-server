package com.authorization.sample.awscognitospringauthserver.web.response;

import lombok.*;

/**
 * <h2>BaseResponse</h2>
 *
 * @author aek
 * <p>
 * Description:
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BaseResponse {

    private Object data;
    private String message;
    private boolean error = true;

    public BaseResponse(Object data, String message) {
        this.data = data;
        this.message = message;
    }

}
