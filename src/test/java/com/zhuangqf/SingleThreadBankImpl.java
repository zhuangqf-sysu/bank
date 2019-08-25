package com.zhuangqf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 单线程实现版，用于测试
 * @author zhuangqf
 * @date 2019/8/25
 */
public class SingleThreadBankImpl implements Bank {

    private static final HashMap<String,Integer> map = new HashMap<>();

    @Override
    public Integer view(String account) {
        return map.getOrDefault(account,0);
    }

    @Override
    public Boolean save(String account, Integer money) {
        Integer value = map.getOrDefault(account,0);
        map.put(account,value + money);
        return true;
    }

    @Override
    public Boolean get(String account, Integer money) {
        Integer value = map.getOrDefault(account,0);
        map.put(account,value - money);
        return true;
    }

    @Override
    public Boolean transfer(String fromAccount, String toAccount, Integer money) {
        if(fromAccount.equals(toAccount)) {
            return true;
        }
        Integer value1 = map.getOrDefault(fromAccount,0);
        Integer value2 = map.getOrDefault(toAccount,0);
        map.put(fromAccount,value1-money);
        map.put(toAccount,value2+money);
        return true;
    }

    @Override
    public Map<String, Integer> viewAll() {
        return Collections.unmodifiableMap(map);
    }
}
