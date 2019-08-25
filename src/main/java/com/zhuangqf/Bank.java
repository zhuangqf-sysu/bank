package com.zhuangqf;

import java.util.Map;

/**
 * 模拟银行存取、转账操作
 * money以分为单位
 * @author zhuangqf
 * @date 2019/8/25
 */
public interface Bank {

    /**
     * 最大余额
     */
    int MAX_BALANCE = 1000 * 10000 * 100;

    /**
     * 查看余额
     * @param account 账号，不允许重复，默认合法
     * @return 余额，以分为单位
     */
    Integer view(String account);

    /**
     * 存钱
     * @param account 账号
     * @param money 金额
     * @return 操作是否成功
     */
    Boolean save(String account,Integer money);

    /**
     * 取款
     * @param account 账号
     * @param money 金额
     * @return 操作是否成功
     */
    Boolean get(String account,Integer money);

    /**
     * 转账
     * @param fromAccount 来源账户
     * @param toAccount 目标账户
     * @param money 金额
     * @return 操作是否成功
     */
    Boolean transfer(String fromAccount, String toAccount, Integer money);

    /**
     * 返回全部账户信息
     * @return
     */
    Map<String,Integer> viewAll();
}
