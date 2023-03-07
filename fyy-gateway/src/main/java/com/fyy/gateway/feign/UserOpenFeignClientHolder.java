package com.fyy.gateway.feign;

import com.fyy.common.tools.global.Result;
import com.fyy.security.user.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @author linyancheng
 * @Description feign异步调用包装类
 * @since 2022/8/2
 */
@Slf4j
@Component
public class UserOpenFeignClientHolder {

    @Async
    public Future<Result<UserDetail>> resource(String language, String token, String requestUri, String method){
        return new AsyncResult<>(null);
    }

}
