package com.dalididilo.dahua;

import com.netsdk.demo.module.LoginModule;
import com.netsdk.lib.NetSDKLib;
import com.netsdk.lib.ToolKits;
import com.sun.javafx.util.Utils;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import javax.swing.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.netsdk.lib.NetSDKLib.EM_NET_RECORD_TYPE.NET_RECORD_ACCESSCTLCARD;
import static com.netsdk.lib.NetSDKLib.EM_NET_SCADA_POINT_TYPE.EM_NET_SCADA_POINT_TYPE_YC;
import static com.netsdk.lib.NetSDKLib.NET_DEVSTATE_DEV_RECORDSET_EX;
import static com.netsdk.lib.NetSDKLib.NET_DEVSTATE_SCADA_POINT_LIST;

public class Test {

    // 设备断线通知回调
    private static DisConnect disConnect       = new DisConnect();

    // 网络连接恢复
    private static HaveReConnect haveReConnect = new HaveReConnect();

    public static void main(String args[]){
        // 初始化
        LoginModule.init(disConnect, haveReConnect);
        // 登录
        if (LoginModule.login(new String("192.168.1.108"),Integer.parseInt("37777")
                ,new String("admin"),new String("py123456"))) {
            NetSDKLib.NET_IN_SCADA_ATTACH_INFO info = new NetSDKLib.NET_IN_SCADA_ATTACH_INFO();
            info.emPointType = NetSDKLib.EM_NET_SCADA_POINT_TYPE.EM_NET_SCADA_POINT_TYPE_YC;
            info.cbCallBack = new NetSDKLib.fSCADAAttachInfoCallBack() {
                @Override
                public void invoke(NetSDKLib.LLong lLong, NetSDKLib.LLong lLong1
                        , NetSDKLib.NET_SCADA_NOTIFY_POINT_INFO_LIST net_scada_notify_point_info_list
                        , int i, Pointer pointer) {
                    Pattern pattern = Pattern.compile("([^\u0000]*)");
                    int deviceNum = 0;
                    System.out.println("监测点位个数:::" + net_scada_notify_point_info_list.nList);
                    for (NetSDKLib.NET_SCADA_NOTIFY_POINT_INFO net_scada_notify_point_info : net_scada_notify_point_info_list.stuList) {
                        System.out.println("####Device"+ ++deviceNum +"Begin####");
                        List<String> byte64ToUtf8 = new ArrayList<>();
                        byte64ToUtf8.add(new String(net_scada_notify_point_info.szDevName));
                        byte64ToUtf8.add(new String(net_scada_notify_point_info.szSensorID));
                        byte64ToUtf8.add(new String(net_scada_notify_point_info.szFSUID));
                        byte64ToUtf8.add(new String(net_scada_notify_point_info.szID));
                        byte64ToUtf8.add(new String(net_scada_notify_point_info.szPointName));
                        for (String s : byte64ToUtf8) {
                            if(!"".equals(s)){
                                Matcher matcher = pattern.matcher(s);
                                if(matcher.find(0)){
                                    System.out.println(matcher.group(1));
                                }
                            }
                        }
                        System.out.println("采集值:::" + net_scada_notify_point_info.fValue + ":::" +  + net_scada_notify_point_info.nValue);
                        System.out.println("采集时间:::" + net_scada_notify_point_info.stuCollectTime.toString());
                        System.out.println("####Device End####\n");
                    }
                }
            };
            NetSDKLib.NET_OUT_SCADA_ATTACH_INFO out = new NetSDKLib.NET_OUT_SCADA_ATTACH_INFO();

            LoginModule.netsdk.CLIENT_SCADAAttachInfo(LoginModule.m_hLoginHandle,
                    info,out , 10000);

        }
    }

    /////////////////面板///////////////////
    // 设备断线回调: 通过 CLIENT_Init 设置该回调函数，当设备出现断线时，SDK会调用该函数
    private static class DisConnect implements NetSDKLib.fDisConnect {
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("Device[%s] Port[%d] DisConnect!\n", pchDVRIP, nDVRPort);
        }
    }

    // 网络连接恢复，设备重连成功回调
    // 通过 CLIENT_SetAutoReconnect 设置该回调函数，当已断线的设备重连成功时，SDK会调用该函数
    private static class HaveReConnect implements NetSDKLib.fHaveReConnect {
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("ReConnect Device[%s] Port[%d]\n", pchDVRIP, nDVRPort);
        }
    }
}
