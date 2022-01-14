
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.handshake.client.C00Handshake
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.client.CPacketKeepAlive
 *  net.minecraft.network.play.server.SPacketChat
 *  net.minecraft.network.play.server.SPacketKeepAlive
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.mixin.mixins.accessors.IC00Handshake;
import me.earth.phobos.util.TextUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PingBypass
extends Module {
    private static PingBypass instance;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final TimerUtil pingTimer = new TimerUtil();
    private final List<Long> pingList = new ArrayList<Long>();
    public Setting<String> ip = this.register(new Setting<String>("PhobosIP", "0.0.0.0.0"));
    public Setting<String> port = this.register(new Setting<String>("Port", "0").setRenderName(true));
    public Setting<String> serverIP = this.register(new Setting<String>("ServerIP", "AnarchyHvH.eu"));
    public Setting<Boolean> noFML = this.register(new Setting<Boolean>("RemoveFML", false));
    public Setting<Boolean> getName = this.register(new Setting<Boolean>("GetName", false));
    public Setting<Boolean> average = this.register(new Setting<Boolean>("Average", false));
    public Setting<Boolean> clear = this.register(new Setting<Boolean>("ClearPings", false));
    public Setting<Boolean> oneWay = this.register(new Setting<Boolean>("OneWay", false));
    public Setting<Integer> delay = this.register(new Setting<Integer>("KeepAlives", 10, 1, 50));
    private long currentPing;
    private long serverPing;
    private StringBuffer name;
    private long averagePing;
    private String serverPrefix = "idk";

    public PingBypass() {
        super("PingBypass", "Manages Phobos`s internal Server.", Module.Category.CLIENT, false, false, true);
        instance = this;
    }

    public static PingBypass getInstance() {
        if (instance == null) {
            instance = new PingBypass();
        }
        return instance;
    }

    public String getPlayerName() {
        if (this.name == null) {
            return null;
        }
        return this.name.toString();
    }

    public String getServerPrefix() {
        return this.serverPrefix;
    }

    @Override
    public void onLogout() {
        this.averagePing = 0L;
        this.currentPing = 0L;
        this.serverPing = 0L;
        this.pingList.clear();
        this.connected.set(false);
        this.name = null;
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)event.getPacket();
            if (packet.chatComponent.getUnformattedText().startsWith("@Clientprefix")) {
                this.serverPrefix = packet.chatComponent.getFormattedText().replace("@Clientprefix", "");
            }
        }
    }

    @Override
    public void onTick() {
        if (mc.getConnection() != null && this.isConnected()) {
            if (this.getName.getValue().booleanValue()) {
                mc.getConnection().sendPacket((Packet)new CPacketChatMessage("@Servername"));
                this.getName.setValue(false);
            }
            if (this.serverPrefix.equalsIgnoreCase("idk") && PingBypass.mc.world != null) {
                mc.getConnection().sendPacket((Packet)new CPacketChatMessage("@Servergetprefix"));
            }
            if (this.pingTimer.passedMs(this.delay.getValue() * 1000)) {
                mc.getConnection().sendPacket((Packet)new CPacketKeepAlive(100L));
                this.pingTimer.reset();
            }
            if (this.clear.getValue().booleanValue()) {
                this.pingList.clear();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketKeepAlive alive;
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packetChat = (SPacketChat)event.getPacket();
            if (packetChat.getChatComponent().getFormattedText().startsWith("@Client")) {
                this.name = new StringBuffer(TextUtil.stripColor(packetChat.getChatComponent().getFormattedText().replace("@Client", "")));
                event.setCanceled(true);
            }
        } else if (event.getPacket() instanceof SPacketKeepAlive && (alive = (SPacketKeepAlive)event.getPacket()).getId() > 0L && alive.getId() < 1000L) {
            this.serverPing = alive.getId();
            this.currentPing = this.oneWay.getValue() != false ? this.pingTimer.getPassedTimeMs() / 2L : this.pingTimer.getPassedTimeMs();
            this.pingList.add(this.currentPing);
            this.averagePing = this.getAveragePing();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        IC00Handshake packet;
        if (event.getPacket() instanceof C00Handshake && (packet = (IC00Handshake)event.getPacket()).getIp().equals(this.ip.getValue())) {
            packet.setIp(this.serverIP.getValue());
            System.out.println(packet.getIp());
            this.connected.set(true);
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.averagePing + "ms";
    }

    private long getAveragePing() {
        if (!this.average.getValue().booleanValue() || this.pingList.isEmpty()) {
            return this.currentPing;
        }
        int full = 0;
        for (long i : this.pingList) {
            full = (int)((long)full + i);
        }
        return full / this.pingList.size();
    }

    public boolean isConnected() {
        return this.connected.get();
    }

    public int getPort() {
        int result;
        try {
            result = Integer.parseInt(this.port.getValue());
        }
        catch (NumberFormatException e) {
            return -1;
        }
        return result;
    }

    public long getServerPing() {
        return this.serverPing;
    }
}

