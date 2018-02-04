package JUnit;

import PacketSender.IResultHandler;
import PacketSender.ISystemSender;
import PacketSender.Packet;

public class MockClient implements ISystemSender {
	private Packet packet;
	@Override
	public void start() {
		
	}

	@Override
	public void registerHandler(IResultHandler iResultHandler) {
		
		
	}

	@Override
	public void setPacket(Packet packet) {
		this.packet = packet;		
	}
}
