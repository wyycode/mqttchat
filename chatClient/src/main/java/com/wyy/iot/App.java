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

    private static final Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");


    public static void main(String[] args) throws Exception {


        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入您的英文昵称:");
        String name = scanner.nextLine();
        if (!pattern.matcher(name).matches()) {
            System.out.println("格式要求为英文字母加数字, 请输入您的英文昵称");
            System.exit(1);
        }
        if ("createGroup".equals(name)) {
            System.out.println("这是关键字");
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
            } else if (!line.startsWith("/")) {
                System.out.println(Constants.USE_TIPS);
            } else if ("/quit".equals(line)) {
                System.exit(0);
            } else if (line.startsWith("/group/")) {
                line = line.substring("/group/".length());

                processMsg(line, mqtt, Constants.GROUP_PREFIX, name);
            } else if (line.startsWith("/createGroup")) {
                //TODO create topic
            } else {
                line = line.substring(1);
                processMsg(line, mqtt, Constants.P2P_PREFIX, name);
            }
        }
    }

    private static void processMsg(String line, Mqtt mqtt, String type, String from) {
        String[] arg = line.split(" ");
        if (arg.length < 2) {
            System.out.println(Constants.USE_TIPS);
            return;
        }
        String groupName = arg[0];
        String msg = line.substring(arg[0].length() + 1);
        mqtt.sendMsg(type + groupName, "from " + from + ":" + msg);
    }
}
