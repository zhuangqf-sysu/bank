package com.zhuangqf;

import java.io.*;
import java.util.Random;

import java.util.concurrent.Callable;

/**
 * @author zhuangqf
 * @date 2019/8/25
 */
public class ActionTask implements Callable<Boolean> {

    private final File file;
    private final Bank bank;
    private final Integer actionNum;
    private final Integer accountNum;
    private final Random random;

    public ActionTask(Bank bank, File file,Integer actionNum,Integer accountNum) throws Exception {
        this.bank = bank;
        this.file = file;
        this.actionNum = actionNum;
        this.accountNum = accountNum;
        this.random = new Random();
        if(!file.exists()) {
            if(!file.createNewFile()) {
                throw new Exception("生成文件"+file.getName()+"失败");
            }
        }
    }

    @Override
    public Boolean call() throws Exception {
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(this.file))) {
            for (int i = 0; i < actionNum; i++) {
                Integer action = random.nextInt(3);
                String account = "";
                Integer money = 0;
                switch (action) {
                    case 0:
                        // 存钱
                        account = "account" + random.nextInt(accountNum);
                        money = random.nextInt(Bank.MAX_BALANCE);
                        if (bank.save(account, money)) {
                            writer.println(action + " " + account + " " + money);
                        }
                        break;
                    case 1:
                        // 取钱
                        account = "account" + random.nextInt(accountNum);
                        money = random.nextInt(Bank.MAX_BALANCE);
                        if (bank.get(account, money)) {
                            writer.println(action + " " + account + " " + money);
                        }
                        break;
                    case 2:
                        // 转账
                        String fromAccount = "account" + random.nextInt(accountNum);
                        String toAccount = "account" + random.nextInt(accountNum);
                        money = random.nextInt(Bank.MAX_BALANCE);
                        if (bank.transfer(fromAccount, toAccount, money)) {
                            writer.println(action + " " + account + " " + fromAccount + " " + toAccount + " " + money);
                        }
                        break;
                    default:
                }
            }
        }
        return true;
    }
}
