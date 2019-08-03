package netty;

import io.netty.channel.Channel;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import proto.RoleProto.LoginReq_1001001;
import cn.xiaosheng.ProtoTest.ProtoTestTool;

import com.google.protobuf.Message;

@SuppressWarnings("rawtypes")
public class ProtoManager {
	
	private static ProtoManager ins = new ProtoManager();
	
	private TreeMap<Integer, Class<?>> reqMap;
    private Map<Integer, Class<?>> respMap;
    
    private Channel channel;
	
	public static ProtoManager getInstance(){
		return ins;
	}

	public void initAllProtocol(){
		String packageName = "proto";
        Class clazz = Message.class;
        try {
        	reqMap = ClassUtils.getClasses(packageName, clazz, "Req_");
        	respMap = ClassUtils.getClasses(packageName, clazz, "Resp_");
        } catch (Throwable e) {
            e.printStackTrace();
        }
	}
    
    public TreeMap<Integer, Class<?>> getReqMap() {
    	return reqMap;
    }
    
    public Map<Integer, Class<?>> getRespMap(){
    	return respMap;
    }
	
	public void setChannel(Channel ch){
		channel = ch;
	}
	
	public Channel getChannel(){
		return channel;
	}
	
	public void send(Message msg) {
		if (channel == null || msg == null || !channel.isWritable()) {
			return;
		}
		int cmd = ProtoManager.getInstance().getMessageID(msg);
		Packet packet = new Packet(Packet.HEAD_TCP, cmd, msg.toByteArray());
		channel.writeAndFlush(packet);
	}
	
	public int getMessageID(Message msg) {
		int protocol = 0;
        Set<Entry<Integer, Class<?>>> set = reqMap.entrySet();
        for (Entry<Integer, Class<?>> entry : set) {
            if (entry.getValue().isInstance(msg)) {
                protocol = entry.getKey();
                break;
            }
        }
        return protocol;
	}
	
	//返回协议处理
    public void handleProto(Packet packet){
    	//打印协议
    	Class<?> clz = getRespMap().get(packet.getCmd());
		try {
			Method method = clz.getMethod("parseFrom", byte[].class);
			Object object = method.invoke(clz, packet.getBytes());
			
			String resp = ProtoPrinter.parseResps(object);
			ProtoTestTool.print(packet.getCmd(), resp);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
	
	public void login(String host, String port){
		NettyTcpClient.instance().conect(host, Integer.valueOf(port));

		System.out.println("connet host:"+host+" success!");
		
		LoginReq_1001001.Builder builder = LoginReq_1001001.newBuilder();
		builder.setAccount("xiaosheng996");
		builder.setPassword("jianshu");
		send(builder.build());
	}
}
