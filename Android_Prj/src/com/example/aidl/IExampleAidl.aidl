package com.example.aidl;
import com.example.aidl.ICallBackAidl;
interface IExampleAidl{
  boolean getServiceState();
  void registerCallback(ICallBackAidl iCallBack);
  void unRegisterCallback(ICallBackAidl iCallBack);
}