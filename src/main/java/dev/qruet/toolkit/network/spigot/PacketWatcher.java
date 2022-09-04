package dev.qruet.toolkit.network.spigot;

import dev.qruet.toolkit.utility.Try;
import io.netty.channel.*;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Source modified and pulled from {@see <a href="https://www.spigotmc.org/threads/cancel-packet.429156/">LoxleyShadow's Spigot Thread</a>}
 *
 * @author qruet
 * @author LoxleyShadow
 */
public class PacketWatcher implements Listener {

    private PacketWatcher() {
        throw new UnsupportedOperationException("This utility class cannot be initialized.");
    }

    public static void unregisterPacketHandler(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("1a4a.channel_handler." + player.getName());
            return null;
        });
    }

    public static void registerPacketHandler(PacketListener listener, Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
                if (obj instanceof Packet packet) {
                    PacketEvent event = new PacketEvent(packet);
                    listener.ingoing(event);
                    if (event.isCancelled())
                        return;
                }
                super.channelRead(channelHandlerContext, obj);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object obj, ChannelPromise channelPromise) throws Exception {
                if (obj instanceof Packet packet) {
                    PacketEvent event = new PacketEvent(packet);
                    listener.outgoing(event);
                    if (event.isCancelled())
                        return;
                }
                super.write(channelHandlerContext, obj, channelPromise);
            }


        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        unregisterPacketHandler(player);

        pipeline.addBefore("packet_handler", "1a4a.channel_handler." + player.getName(), channelDuplexHandler);

    }

    public abstract static class PacketListener {
        @Retention(RetentionPolicy.RUNTIME)
        public @interface PacketHandler {
            enum HandlerType {
                INGOING, OUTGOING
            }

            HandlerType handlerType();
        }

        private List<Consumer<PacketEvent>> ingoing_funcs;
        private List<Consumer<PacketEvent>> outgoing_funcs;

        public PacketListener() {
            this.ingoing_funcs = new ArrayList<>();
            this.outgoing_funcs = new ArrayList<>();

            for (Method method : getClass().getDeclaredMethods()) {
                method.setAccessible(true);
                PacketHandler annotation = (PacketHandler) Arrays.stream(method.getAnnotations()).filter(an -> an.annotationType() == PacketHandler.class).findAny().orElse(null);
                if (annotation == null)
                    return;
                switch (annotation.handlerType()) {
                    case INGOING:
                        ingoing_funcs.add((p) -> Try.Catch(() -> method.invoke(this, p),
                                IllegalAccessException.class, IllegalArgumentException.class, InvocationTargetException.class));
                        break;
                    case OUTGOING:
                        outgoing_funcs.add((p) -> Try.Catch(() -> method.invoke(this, p),
                                IllegalAccessException.class, IllegalArgumentException.class, InvocationTargetException.class));
                        break;
                }
            }
        }

        private void ingoing(PacketEvent event) {
            ingoing_funcs.forEach(c -> c.accept(event));
        }

        private void outgoing(PacketEvent event) {
            outgoing_funcs.forEach(c -> c.accept(event));
        }
    }

    public static class PacketEvent {

        private boolean cancelled;
        private Packet<?> packet;

        public PacketEvent(Packet<?> packet) {
            this.packet = packet;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean val) {
            this.cancelled = val;
        }

        public Packet<?> getPacket() {
            return packet;
        }

    }

}
