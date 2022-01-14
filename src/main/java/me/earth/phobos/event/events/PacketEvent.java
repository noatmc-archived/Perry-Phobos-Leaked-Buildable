/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraftforge.fml.common.eventhandler.Cancelable
 */
package me.earth.phobos.event.events;

import me.earth.phobos.event.EventStage;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class PacketEvent
extends EventStage {
    private final Packet<?> packet;

    public PacketEvent(int stage, Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T)this.packet;
    }

    @Cancelable
    public static class Receive
    extends PacketEvent {
        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Send
    extends PacketEvent {
        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }
}

