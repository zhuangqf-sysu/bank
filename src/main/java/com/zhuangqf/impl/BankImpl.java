package com.zhuangqf.impl;

import com.zhuangqf.Bank;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhuangqf
 * @date 2019/8/25
 */
public class BankImpl implements Bank {

    /**
     * 最多支持1000个账号，暂不考虑扩容问题
     */
    private static final int MAX_ACCOUNT = 1000;

    /**
     * 账号余额
     */
    private final Node[] balances = new Node[MAX_ACCOUNT];

    /**
     * key: 账号
     * value： 余额的index
     * balance = balances[indexMap.get(key)]
     * 使用ConcurrentHashMap是为了保证在多线程下扩容不出错
     */
    private final ConcurrentHashMap<String,Integer> indexMap = new ConcurrentHashMap<String, Integer>();

    /**
     * index 累加器，用来生成index
     */
    private final AtomicInteger indexCounter = new AtomicInteger();

    @Override
    public Integer view(String account) {
        Integer index = indexMap.get(account);
        if(index == null || balances[index] == null) {
            return 0;
        }
        return balances[index].balance;
    }

    @Override
    public Boolean save(String account, Integer money) {
        Integer index = getIndex(account);
        synchronized (balances[index]) {
            if(balances[index].balance + money > MAX_BALANCE) {
                return false;
            }
            balances[index].balance += money;
            return true;
        }
    }

    @Override
    public Boolean get(String account, Integer money) {
        Integer index = getIndex(account);
        synchronized (balances[index]) {
            if(balances[index].balance < money) {
                return false;
            }
            balances[index].balance -= money;
            return true;
        }
    }

    @Override
    public Boolean transfer(String fromAccount, String toAccount, Integer money) {
       Integer index1 = getIndex(fromAccount);
       Integer index2 = getIndex(toAccount);
       if(index1.equals(index2)) {
           return true;
       }
       // 先锁小index的账号，防止死锁
       Integer lock1 = index1 > index2 ? index1 : index2;
       Integer lock2 = index1 > index2 ? index2 : index1;
       synchronized (balances[lock1]) {
           synchronized (balances[lock2]) {
               if(balances[index1].balance < money) {
                   return false;
               }
               if(balances[index2].balance + money > MAX_BALANCE) {
                   return false;
               }
               balances[index1].balance -= money;
               balances[index2].balance += money;
               return true;
           }
       }
    }

    @Override
    public Map<String, Integer> viewAll() {
        Map<String,Integer> map = new HashMap<>(indexMap.size());
        indexMap.forEach((account,index) -> map.put(account,balances[index].balance));
        return map;
    }

    private Integer getIndex(String account) {
        if(indexMap.containsKey(account)) {
            return indexMap.get(account);
        }
        // 利用 ConcurrentHashMap的分段锁
        return indexMap.compute(account,(key,value) -> {
            if(value == null) {
                int index = indexCounter.getAndIncrement();
                balances[index] = new Node();
                return index;
            }else{
                return value;
            }
        });
    }

    private static class Node {
        volatile int balance = 0;
    }
}
