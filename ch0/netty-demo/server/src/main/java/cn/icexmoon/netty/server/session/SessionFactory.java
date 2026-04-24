package cn.icexmoon.netty.server.session;

import lombok.Getter;

/**
 * @author sidiot
 */
public abstract class SessionFactory {

    @Getter
    private static Session session = new SessionMemoryImpl();

}
