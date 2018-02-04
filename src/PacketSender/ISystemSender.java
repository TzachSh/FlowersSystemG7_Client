package PacketSender;

public interface ISystemSender {
	void start();

	void registerHandler(IResultHandler iResultHandler);
	void setPacket(Packet packet);
}
