package com.DarkEG.Core.Msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsgHandler {
	Map<MessageFlags, List<MsgReceiver>> recievers = new HashMap<MessageFlags, List<MsgReceiver>>();
	
	public void register(MessageFlags flag, MsgReceiver reciever){
		if(recievers.containsKey(flag)){
			List<MsgReceiver> list = recievers.get(flag);
			list.add(reciever);
		} else {
			List<MsgReceiver> list = new ArrayList<>();
			list.add(reciever);
			recievers.put(flag, list);
		}
	}
	
	public void sendMsg(MessageFlags flag, String msg){
		List<MsgReceiver> list = recievers.get(flag);
		for(MsgReceiver rec : list){
			rec.recieve(flag, msg);
		}
	}
	public void cleanUp(){
		for(List<MsgReceiver> list : recievers.values()){
			list.clear();
		}
		recievers.clear();
	}
}
