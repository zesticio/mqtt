package com.zestic.mqtt.server.netty;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PublishMessage implements Serializable {

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
