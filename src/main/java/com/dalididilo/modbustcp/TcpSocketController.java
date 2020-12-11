package com.dalididilo.modbustcp;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.*;


/**
 * Socket 控制层。
 * @author dalididilo
 * @date 2020-12-07 10:13:07
 */
public class TcpSocketController{

    private Socket socket = new Socket();
    /**
     * 地址和端口号。
     */
    private final static String IP = "192.168.1.230";
    private final static String PORT = "6003";

    private String str="HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(str);
    /**
     * 心跳包。
     * @description 心跳包发送后，网关对指令的请求才会响应状态
     */
    private final static String HEART_BEAT = "SET;FFFFFFFF;{230,251.0.1};\r\n";

    private final static Long BASE_NUM = Long.parseLong("00010102", 16);
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);


    private ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("pool-%d").build();
    private ExecutorService execPool = new ThreadPoolExecutor(1,200,0L, TimeUnit.MILLISECONDS
            , new LinkedBlockingDeque<Runnable>(1024),factory,new ThreadPoolExecutor.AbortPolicy());
    /**
     * 输入输出流。
     */
    private InputStream inputStream = null;
    private BufferedReader bufferedReader = null;
    private OutputStream outputStream = null;
    public static void main(String[] args){
        TcpSocketController tcpClient = new TcpSocketController();
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("输入1、0,选择打开或关闭功能: ");
            int operator = scanner.nextInt();
            System.out.print("输入1 - 8,下达指令。: ");
            int num = scanner.nextInt();
            tcpClient.sendCommand(String.format("SET;0000000%d;{230.0.1.%d};\r\n",operator,num),"GBK");
            System.out.print("输入exit退出，任意输入则继续:\r\n\n");
            if ("exit".equals(scanner.next())) {
                break;
            }
        }
        try {
            tcpClient.executorService.shutdown();
            tcpClient.socket.shutdownInput();
            tcpClient.socket.shutdownOutput();
            tcpClient.inputStream.close();
            tcpClient.bufferedReader.close();
            tcpClient.outputStream.close();
            tcpClient.socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 每3s保持与modbus-TCP的心跳连接。
     */
    private void keepHeartBeat(){
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (socket.isConnected()) {
                    try {
//                        System.out.println(dateFormat.format(System.currentTimeMillis()) + " : 保持心跳");
                        outputStream.write(HEART_BEAT.getBytes("GBK"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    initConnect(IP,PORT);
                }
            }
        },1,3, TimeUnit.SECONDS);
    }

    /**
     * 构造函数。
     */
    public TcpSocketController() {
        try {
            socket.setTcpNoDelay(true);
            socket.setReuseAddress(true);
            socket.setSoLinger(true, 5);
            socket.setSendBufferSize(1024);
            socket.setReceiveBufferSize(1024);
            socket.setKeepAlive(true);
            initConnect(IP,PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化链接。
     * @param ip
     * @param port
     */
    private void initConnect(String ip, String port){
        try {
            socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 30000);
            if (socket.isConnected()) {
                outputStream = socket.getOutputStream();
                keepHeartBeat();
                initReadResponse();
            }else{
                initConnect(IP,PORT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化modbus-TCP请求响应线程。
     */
    private void initReadResponse(){
        execPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    inputStream = socket.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while((line = bufferedReader.readLine()) != null){
                        convertResponse(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 解析响应数据。
     * @param response
     */
    private void convertResponse(String response){
        if (response != null && !"".equals(response) && !"\r\n".equals(response)) {
            /**
             * 8路20A开关电流监测模块
             * 响应实例，根据;分隔。
             * 0:FB响应头。
             * 1:00000001,16进制数
             * 2:{230.0.1.(
             *   <=8对应值为具体开关号,16进制数0代表关闭、1代表开启。
             *   >8则取余8对应的值分别对应开关号的检测电流。)}
             * FB;00000001;{230.0.1.1};\r\n
             */
//            System.out.println(response);
            if (response.indexOf("FB") != -1) {
                String[] split = response.split(";");
                // 16进制转10进制。
                String point = split[2];
                String substring = point.substring(point.indexOf("{") + 1, point.indexOf("}"));
                String[] pointSplit = substring.split("\\.");
                // 具体功能位。
                boolean isCustomer = Integer.parseInt(pointSplit[2]) >= 3;
                int operatorPoint = Integer.parseInt(pointSplit[3]);
                long ma = Long.parseLong(split[1], 16);
                if (operatorPoint > 8) {
                    int num = operatorPoint % 8;
                    String console = String.format("状态:开关回路%d:电流为:%dmA",num,ma);
                    System.out.println(console);
                }else{
                    if (isCustomer){
                        /**
                         * 不做响应的用户操作
                         * 257/258 激活触摸屏
                         * 68353L/68354L 下一页
                         * 133889L/133890L 上一页
                         */
                        if (ma == 257L || ma == 258L
                                || ma == 68353L || ma == 68354L
                                || ma == 133889L || ma == 133890L) {
                            return;
                        }
                        long switchNum = Math.abs((ma - BASE_NUM) / 256) + 1;
                        if (switchNum >= 257) {
                            /**
                             * 面板2的操作，暂无。
                             */
                            Long baseNum = 8L;
                            System.out.println(String.format("操作:#用户# 控制开关回路%d"
                                    ,switchNum % 256L + baseNum));
                        }else if (switchNum == 9) {
                            System.out.println(String.format("操作:#用户# 控制开关回路%s","全开"));
                        }else if(switchNum == 10){
                            System.out.println(String.format("操作:#用户# 控制开关回路%s","全关"));
                        }else{
                            System.out.println(String.format("操作:#用户# 控制开关回路%d",switchNum));
                        }
                    }else{
                        String console = String.format("状态:开关回路%d:%s",operatorPoint,ma > 0 ? "打开" : "关闭");
                        System.out.println(console);
                    }
                }
            }
        }
    }

    /**
     * 发送指令。
     * @param req
     * @param charSet
     */
    private void sendCommand(String req,String charSet){
        if (!socket.isConnected()) {
            initConnect(IP,PORT);
        }else{
            try {
                outputStream.write(HEART_BEAT.getBytes("GBK"));
                outputStream.write(req.getBytes(charSet));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
