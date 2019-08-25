package com.zhuangqf;

import com.zhuangqf.impl.BankImpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author zhuangqf
 * @date 2019/8/25
 */
public class BankTest {

    private static final String FILE_TEST = "./src/test/resources/test";

    private static final Integer TEST_NUM = 64;
    private static final Integer ACTION_NUM = 1000000;
    private static final Integer ACCOUNT_NUM = 100;

    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(TEST_NUM);
        List<Future<Boolean>> futureList = new ArrayList<>(TEST_NUM);

        Bank bank = new BankImpl();
        Long begin = System.currentTimeMillis();
        for(int i=0;i<TEST_NUM;i++) {
            Future<Boolean> future = executorService.submit(
                    new ActionTask(bank,new File(FILE_TEST+i),ACTION_NUM,ACCOUNT_NUM));
            futureList.add(future);
        }
        for(Future future : futureList) {
            future.get();
        }
        executorService.shutdown();

        Long end = System.currentTimeMillis();
        System.out.println("BankImpl 执行结束，共耗时："+(end - begin)+"ms");

        Map<String,Integer> resultMap = getResult();
        boolean result = testResult(resultMap,bank);
        System.out.println("BankImpl 测试"+(result?"通过":"失败"));
    }

    private static boolean testResult(Map<String, Integer> resultMap, Bank bank) {
        for(Map.Entry<String,Integer> entry : resultMap.entrySet()) {
            if(!entry.getValue().equals(bank.view(entry.getKey()))) {
                System.out.println("测试失败：" + entry.getKey() + " bank:" + bank.view(entry.getKey())
                        + " result:" + entry.getValue());
                return false;
            }
        }
        return true;
    }

    /**
     * 串行读取文件，获取结果
     * @return
     */
    private static Map<String, Integer> getResult() throws Exception {
        Bank bank = new SingleThreadBankImpl();
        for(int i=0;i<TEST_NUM;i++) {
            File file = new File(FILE_TEST + i);
            try(Scanner scanner = new Scanner(new FileInputStream(file))) {
                while (scanner.hasNext()) {
                    Integer action = scanner.nextInt();
                    String account = "";
                    Integer money = 0;
                    switch (action) {
                        case 0:
                            // 存钱
                            account = scanner.next();
                            money = scanner.nextInt();
                            bank.save(account, money);
                            break;
                        case 1:
                            // 取钱
                            account = scanner.next();
                            money = scanner.nextInt();
                            bank.get(account, money);
                            break;
                        case 2:
                            // 转账
                            String fromAccount = scanner.next();
                            String toAccount = scanner.next();
                            money = scanner.nextInt();
                            bank.transfer(fromAccount, toAccount, money);
                            break;
                        default:
                    }
                }
            }
        }
        return bank.viewAll();
    }
}
