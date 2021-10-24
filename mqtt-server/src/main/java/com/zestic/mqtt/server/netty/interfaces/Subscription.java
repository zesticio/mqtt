package com.zestic.mqtt.server.netty.interfaces;

import java.util.List;
import java.util.Set;

public interface Subscription extends ControlPacket {
    /**
     * Add a subscription
     */
    boolean add(Subscription subscription, boolean onlyMemory);

    /**
     * Delete the subscription
     */
    boolean remove(Subscription subscription);

    /**
     * Matching meet topic all subscribe to the relationship:
     */
    List<Subscription> match(String topic);

    /**
     * According to the client Id find all subscriptions
     *
     * @param clientId
     * @return
     */
    Set<Subscription> findAllBy(String clientId);

    /**
     * According to the client Id delete all subscriptions
     *
     * @param clientId
     */
    void removeAllBy(String clientId);
}
