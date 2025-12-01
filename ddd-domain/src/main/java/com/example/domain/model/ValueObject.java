package com.example.domain.model;

/**
 * 值对象标记接口
 * 值对象是不可变的，通过值相等性进行比较
 */
public interface ValueObject {
    /**
     * 值对象相等性比较
     * @param other 另一个值对象
     * @return 是否相等
     */
    boolean equals(Object other);
    
    /**
     * 值对象哈希码
     * @return 哈希码
     */
    int hashCode();
}

