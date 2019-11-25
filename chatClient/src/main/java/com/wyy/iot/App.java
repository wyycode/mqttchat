package com.wyy.iot;

import jodd.props.Props;
import jodd.util.StringUtil;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public class App {

    private static final Pattern pattern = Pattern.compile("^[A-Za-z0-9]$");


    public static void main(String[] args) throws Exception {


        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入您的英文昵称:");
        String name = scanner.nextLine();
        if (!pattern.matcher(name).matches()) {
            System.out.println("格式要求为英文字母加数字, 请输入您的英文昵称");
            System.exit(1);
        }


        Props props = new Props();

        String userDir = System.getProperty("user.home");

        File file = new File(userDir + "/.chat/app.properties");
        System.out.println(file.exists());
        System.out.println(file.getAbsolutePath());
        props.load(file);

        Mqtt mqtt = new Mqtt(props.getValue("mqtt.url"), name);
        mqtt.test();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                mqtt.shutdown();
                System.out.println("shutdown");
            }
        });

        String line;
        while ((line = scanner.nextLine()) != null) {
            if (StringUtil.isBlank(line)) {
                System.out.println("请重新输入:");
                continue;
            }
            else if ("/quit".equals(line)) {
                System.exit(0);
            }
            else if (line.startsWith("/group/")) {

            }
            System.out.println("你输入的是：" + line);
        }
    }
}
