package org.cocolian.id.service;

/**
 * @description:
 * @author: zn.wang , Created in 11:58 2018/6/24.
 */
public interface DistributedSequence {
    public Long sequence(String sequenceName);
}
